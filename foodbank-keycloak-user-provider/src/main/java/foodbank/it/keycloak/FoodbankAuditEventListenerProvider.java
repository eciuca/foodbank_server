package foodbank.it.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import java.util.Map;

@Slf4j
public class FoodbankAuditEventListenerProvider implements EventListenerProvider {
    private final KeycloakSession session;

    public FoodbankAuditEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (!(EventType.LOGIN == event.getType())) {
            return;
        }
        String ip = event.getIpAddress();
        Map<String, String> details = event.getDetails();
        if (details == null) {
            return;
        }
        String userName = details.get("username");
        if (userName == null) {
            return;
        }
        boolean isBarcodeLogin = details.containsKey("login_method") && "barcode".equals(details.get("login_method"));
        String application = isBarcodeLogin ? "2.0 barcode" : "2.0";
        AuditLogin.auditLogin(userName, ip, application);
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }
}
