package foodbank.it.keycloak;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
public class FoodbankAuditEventListenerProviderFactory implements EventListenerProviderFactory {
    public static final String PROVIDER_ID = "foodbank-audit";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new FoodbankAuditEventListenerProvider(session);
    }

    @Override
    public void init(Config.Scope config) { }

    @Override
    public void postInit(org.keycloak.models.KeycloakSessionFactory factory) { }

    @Override
    public void close() { }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
