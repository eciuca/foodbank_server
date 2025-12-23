package foodbank.it.keycloak;

import foodbank.it.keycloak.utils.AuthConstants;
import foodbank.it.keycloak.utils.EnvironmentConfig;
import foodbank.it.keycloak.utils.UserAdapterUtils;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import jakarta.ws.rs.core.Response;
import java.util.Optional;

/**
 * Authenticator that enforces company migration rules:
 * - Users from migrated companies can only access the NEW app
 * - Users from non-migrated companies can only access the OLD app
 * - Global admins (FBBA + admin group) bypass these checks
 * - Barcode authentication is only available on the NEW app
 */
@Slf4j
public class CompanyMigrationAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();

        if (user == null) {
            context.success();
            return;
        }

        // Try to get UserAdapter - if not our user type, let it pass
        Optional<UserAdapter> optionalAdapter = UserAdapterUtils.fromUserModel(user);
        if (optionalAdapter.isEmpty()) {
            log.debug("User {} is not from Foodbank provider, skipping migration check", user.getUsername());
            context.success();
            return;
        }

        UserAdapter userAdapter = optionalAdapter.get();
        boolean isNewApp = isNewAppRequest(context);
        boolean isBarcodeLogin = isBarcodeLogin(context);
        String companyId = userAdapter.getCompanyIdOptional().orElse("NO_COMPANY");

        // Check 1: Barcode authentication is only available on NEW app
        if (isBarcodeLogin && !isNewApp) {
            log.warn("Access DENIED: User {} attempted barcode login on OLD app - barcode only supported on NEW app",
                user.getUsername());
            failWithError(context, "barcodeNotSupportedOnOldAppError", EnvironmentConfig.NEW_APP_URL);
            return;
        }

        // Global admin bypass (after barcode check - even admins can't use barcode on old app)
        if (userAdapter.isGlobalAdmin()) {
            log.info("Global admin user {} - access granted to all apps", user.getUsername());
            context.success();
            return;
        }

        // Check 2: Migration gate
        boolean companyMigrated = userAdapter.isCompanyMigrated();

        log.info("User {} from company {} (migrated={}), login_method={}, requesting access to {} app",
            user.getUsername(), companyId, companyMigrated, isBarcodeLogin ? "barcode" : "password",
            isNewApp ? "NEW" : "OLD");

        if (isNewApp && !companyMigrated) {
            log.warn("Access DENIED: User {} - company {} not yet migrated to new app",
                user.getUsername(), companyId);
            failWithError(context, "companyNotMigratedError", EnvironmentConfig.OLD_APP_URL);
            return;
        }

        if (!isNewApp && companyMigrated) {
            log.warn("Access DENIED: User {} - company {} already migrated, must use new app",
                user.getUsername(), companyId);
            failWithError(context, "companyAlreadyMigratedError", EnvironmentConfig.NEW_APP_URL);
            return;
        }

        log.info("Access GRANTED for user {} to {} app", user.getUsername(), isNewApp ? "NEW" : "OLD");
        context.success();
    }

    private boolean isBarcodeLogin(AuthenticationFlowContext context) {
        String loginMethod = context.getAuthenticationSession().getUserSessionNotes()
            .get(AuthConstants.LOGIN_METHOD_SESSION_NOTE);
        return AuthConstants.LOGIN_METHOD_BARCODE.equals(loginMethod);
    }

    private void failWithError(AuthenticationFlowContext context, String errorMessageKey, String redirectUrl) {
        Response challenge = context.form()
            .setError(errorMessageKey, redirectUrl)
            .createErrorPage(Response.Status.FORBIDDEN);
        context.failureChallenge(AuthenticationFlowError.ACCESS_DENIED, challenge);
    }

    private boolean isNewAppRequest(AuthenticationFlowContext context) {
        try {
            String clientId = context.getAuthenticationSession().getClient().getClientId();
            log.debug("Client ID: {}", clientId);
            return AuthConstants.NEW_APP_CLIENT_ID.equals(clientId);
        } catch (Exception e) {
            log.error("Error getting client ID, defaulting to old app", e);
            return false;
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // No action needed
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
