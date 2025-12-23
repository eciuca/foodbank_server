package foodbank.it.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jboss.logging.Logger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuditLogin {
    private static final Logger logger = Logger.getLogger(AuditLogin.class);

    private static final String ENV_SECRET_NAME = "BARCODE_SECRET";

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .findAndRegisterModules()                    // JSR-310 (LocalDateTime) support
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601

    public static final String SECRET;

    static {
        SECRET = Optional.ofNullable(System.getenv(ENV_SECRET_NAME))
            .filter(s -> !s.isBlank())
            .orElseThrow(() -> new IllegalStateException(
                "Environment variable " + ENV_SECRET_NAME + " must be set and non-empty"));
    }

    public static void auditLogin(String userId, String ipAddress, String application) {
        try {
            AuditLoginDto dto = AuditLoginDto.build(
                userId,
                LocalDateTime.now(),
                ipAddress,
                application
            );

            String baseUrl = Optional.ofNullable(System.getenv("STOCK_REST_BASE_URL"))
                .filter(s -> s != null && !s.isEmpty())
                .orElse(System.getenv("STOCK_REST_FALLBACK_URL"));

            if (baseUrl == null) {
                return;
            }

            String url = baseUrl.endsWith("/") ? baseUrl + "audits/login" : baseUrl + "/audits/login";

            String canonical = AuditLoginDto.canonicalPipes(dto);
            dto.setHashSign(HMAC.hmacSha256Short(canonical, SECRET, AuditLoginDto.SIGNATURE_LENGTH));

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                MAPPER.writeValue(os, dto); // <— no manual JSON building
            }

            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                logger.warn("Audit POST returned status " + code + " to " + url);
            }
        } catch (Exception e) {
            logger.warn("Error while auditing barcode login: " + e.toString());
        }
    }
}
