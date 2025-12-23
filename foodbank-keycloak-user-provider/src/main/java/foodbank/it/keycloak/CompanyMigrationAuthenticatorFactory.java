package foodbank.it.keycloak;

import foodbank.it.keycloak.utils.EnvironmentConfig;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

/**
 * Factory for CompanyMigrationAuthenticator.
 */
@Slf4j
public class CompanyMigrationAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "foodbank-migration-authenticator";

    private static final CompanyMigrationAuthenticator SINGLETON = new CompanyMigrationAuthenticator();

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Foodbank Company Migration Gate";
    }

    @Override
    public String getReferenceCategory() {
        return "authorization";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Checks if the user's company has been migrated and allows/denies access based on target application.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {
        log.info("Initializing Company Migration Authenticator Factory");

        // Force loading of EnvironmentConfig to fail fast if env vars are missing
        log.info("OLD_APP_URL configured: {}", EnvironmentConfig.OLD_APP_URL);
        log.info("NEW_APP_URL configured: {}", EnvironmentConfig.NEW_APP_URL);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        log.info("Closing Company Migration Authenticator Factory");
    }
}
