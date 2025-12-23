package foodbank.it.keycloak;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HMAC {
    public static String hmacSha256Short(String data, String secret, int signatureLength) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String b64url = Base64.getUrlEncoder().withoutPadding().encodeToString(result);
            return b64url.substring(0, Math.min(signatureLength, b64url.length()));
        } catch (InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException("Error computing HMAC", exception);        }
    }
}
