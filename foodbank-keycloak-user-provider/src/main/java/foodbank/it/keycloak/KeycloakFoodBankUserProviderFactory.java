package foodbank.it.keycloak;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

@Slf4j
public class KeycloakFoodBankUserProviderFactory implements UserStorageProviderFactory<KeycloakFoodBankUserProvider> {

    public static final String PROVIDER_ID = "foodbank-mysql-user-provider";

    public static final String PROVIDER_STORE_ID = "foodbank-mysql-user-store";

    @Override
    public KeycloakFoodBankUserProvider create(KeycloakSession session, ComponentModel model) {
        EntityManager entityManager = session.getProvider(JpaConnectionProvider.class, PROVIDER_STORE_ID).getEntityManager();
        return new KeycloakFoodBankUserProvider(entityManager, model, session);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Foodbank Mysql User Storage Provider";
    }

    @Override
    public void close() {
        log.info("<<<<<< Closing factory");
    }
}
