package foodbank.it.keycloak;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

/**
 * Factory for FoodbankCredentialsAuthenticator.
 * Handles both username/password and barcode authentication.
 */
public class FoodbankCredentialsAuthenticatorFactory implements AuthenticatorFactory {

    // Keep the same ID to avoid changing realm.json
    public static final String ID = "uname-pw-or-bcode-authenticator";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayType() {
        return "Foodbank Credentials (Password or Barcode)";
    }

    @Override
    public String getHelpText() {
        return "Authenticates users via username/password or via barcode (username|expiration|signature format).";
    }

    @Override
    public String getReferenceCategory() {
        return "password";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new FoodbankCredentialsAuthenticator();
    }

    @Override
    public void init(Config.Scope config) {
        // No initialization needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization needed
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public boolean isUserSetupAllowed() {
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
}
