package foodbank.it.keycloak.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Shared constants for authentication.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {

    // Session notes
    public static final String LOGIN_METHOD_SESSION_NOTE = "login_method";

    // Login methods
    public static final String LOGIN_METHOD_BARCODE = "barcode";
    public static final String LOGIN_METHOD_PASSWORD = "password";

    // Client IDs
    public static final String OLD_APP_CLIENT_ID = "stock-app";
    public static final String NEW_APP_CLIENT_ID = "frontend";
}
