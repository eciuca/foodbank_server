package foodbank.it.keycloak;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLoginDto {

    private String userId;
    private LocalDateTime dateTime;
    private String ipAddress;
    private String application;
    private String hashSign;

    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append("\"userId\":\"").append(esc(userId)).append("\",");
        json.append("\"dateTime\":\"")
            .append(dateTime == null ? "" : dateTime.toString())
            .append("\",");
        json.append("\"ipAddress\":")
            .append(ipAddress == null ? "null" : "\"" + esc(ipAddress) + "\"").append(",");
        json.append("\"application\":\"").append(esc(application)).append("\",");
        json.append("\"hashSign\":\"").append(esc(hashSign)).append("\"");
        json.append("}");
        return json.toString();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // --- Convenience builders ---
    public static AuditLoginDto build(String userId,
                                      LocalDateTime dateTime,
                                      String ipAddress,
                                      String application) {
        return AuditLoginDto.builder()
            .userId(userId)
            .dateTime(dateTime)
            .ipAddress(ipAddress)
            .application(application)
            .build();
    }

    public static AuditLoginDto buildNow(String userId,
                                         String ipAddress,
                                         String application) {
        return build(userId, LocalDateTime.now(), ipAddress, application);
    }

    public static final int SIGNATURE_LENGTH = 32;
    public static String canonicalPipes(AuditLoginDto dto) {
        String date = (dto.getDateTime() == null) ? "" : dto.getDateTime().toString();

        return String.join("|",
            nz(dto.getUserId()),
            date,
            nz(dto.getIpAddress()),
            nz(dto.getApplication())
        );
    }

    private static String nz(String s) {
        return (s == null) ? "" : s;
    }
}
