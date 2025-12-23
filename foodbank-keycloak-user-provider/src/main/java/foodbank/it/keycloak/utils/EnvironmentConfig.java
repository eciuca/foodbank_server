package foodbank.it.keycloak.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * Environment configuration for the Keycloak module.
 * Validates and provides access to required environment variables.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentConfig {

    private static final String OLD_APP_URL_ENV = "OLD_APP_URL";
    private static final String NEW_APP_URL_ENV = "NEW_APP_URL";

    public static final String OLD_APP_URL;
    public static final String NEW_APP_URL;

    static {
        OLD_APP_URL = getRequiredEnv(OLD_APP_URL_ENV);
        NEW_APP_URL = getRequiredEnv(NEW_APP_URL_ENV);
    }

    private static String getRequiredEnv(String name) {
        return Optional.ofNullable(System.getenv(name))
            .filter(s -> !s.isBlank())
            .orElseThrow(() -> new IllegalStateException(
                "Environment variable " + name + " must be set and non-empty"));
    }

}
