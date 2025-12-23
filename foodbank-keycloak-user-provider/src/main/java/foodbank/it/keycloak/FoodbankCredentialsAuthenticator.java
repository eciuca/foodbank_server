package foodbank.it.keycloak;

import foodbank.it.keycloak.utils.AuthConstants;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;

/**
 * Authenticator that handles both:
 * - Standard username/password authentication
 * - Barcode-based authentication (username contains | separator)
 * <p>
 * Barcode format: username|expiration(ddMMyy)|signature
 */
@Slf4j
public class FoodbankCredentialsAuthenticator extends UsernamePasswordForm {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyy");
    private static final int SIGNATURE_LENGTH = 12;
    private static final String BARCODE_SEPARATOR = "|";

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = normalizeInput(formData.getFirst("username"));

        if (isBarcodeInput(username)) {
            handleBarcodeAuthentication(context, username);
        } else {
            handlePasswordAuthentication(context);
        }
    }

    private String normalizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
            .replace('^', '|')
            .replace('¦', '|');
    }

    private boolean isBarcodeInput(String username) {
        return username != null && username.contains(BARCODE_SEPARATOR);
    }

    private void handlePasswordAuthentication(AuthenticationFlowContext context) {
        context.getAuthenticationSession().setUserSessionNote(
            AuthConstants.LOGIN_METHOD_SESSION_NOTE, AuthConstants.LOGIN_METHOD_PASSWORD);
        super.action(context);
    }

    private void handleBarcodeAuthentication(AuthenticationFlowContext context, String barcodeInput) {
        String[] parts = barcodeInput.split("\\|");

        if (parts.length != 3) {
            log.warn("Barcode token has incorrect format");
            failWithError(context, "barcodeInvalidFormatError");
            return;
        }

        String barcodeUsername = parts[0];
        String expirationStr = parts[1];
        String signature = parts[2];

        // Validate expiration date
        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expirationStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Expiration date is not a valid ddMMyy date: {}", expirationStr);
            failWithError(context, "barcodeInvalidExpirationError");
            return;
        }

        // Check if expired
        Instant expiryInstant = expiryDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        if (Instant.now().isAfter(expiryInstant)) {
            log.warn("Barcode expired for user: {}", barcodeUsername);
            context.failureChallenge(AuthenticationFlowError.EXPIRED_CODE,
                context.form().setError("barcodeExpiredError").createLoginUsernamePassword());
            return;
        }

        // Validate signature
        String expectedSig = computeSignature(barcodeUsername + "|" + expirationStr);
        if (!expectedSig.equals(signature)) {
            log.warn("Signature mismatch. Provided: {}, Expected: {}", signature, expectedSig);
            failWithError(context, "barcodeInvalidSignatureError");
            return;
        }

        // Find and authenticate user
        UserModel user = context.getSession().users()
            .getUserByUsername(context.getRealm(), barcodeUsername);

        if (user == null) {
            log.warn("No user found for barcodeUsername: {}", barcodeUsername);
            context.failureChallenge(AuthenticationFlowError.UNKNOWN_USER,
                context.form().setError("barcodeUserNotFoundError").createLoginUsernamePassword());
            return;
        }

        context.setUser(user);
        context.getAuthenticationSession().setUserSessionNote(
            AuthConstants.LOGIN_METHOD_SESSION_NOTE, AuthConstants.LOGIN_METHOD_BARCODE);
        context.success();
    }

    private void failWithError(AuthenticationFlowContext context, String messageKey) {
        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,
            context.form().setError(messageKey).createLoginUsernamePassword());
    }

    private String computeSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(AuditLogin.SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result).substring(0, SIGNATURE_LENGTH);
        } catch (Exception e) {
            log.error("Error computing HMAC signature", e);
            throw new RuntimeException("Error computing HMAC", e);
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.challenge(context.form().createLoginUsernamePassword());
    }

    @Override
    public boolean requiresUser() {
        return false;
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
