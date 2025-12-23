package foodbank.it.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter that bridges FoodbankUser entity with Keycloak's UserModel interface.
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private static final Logger logger = Logger.getLogger(UserAdapter.class);

    private static final String FEDERATION_COMPANY_ID = "FBBA";
    private static final String ADMIN_GROUP = "admin";

    protected final FoodbankUser entity;
    protected final String keycloakId;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, FoodbankUser entity) {
        super(session, realm, model);
        this.entity = entity;
        this.keycloakId = StorageId.keycloakId(model, entity.getId());
    }

    // ==================== Authentication Helper Methods ====================

    /**
     * Get the company ID if present.
     */
    public Optional<String> getCompanyIdOptional() {
        return entity.getCompanyOptional().map(FoodbankCompany::getId);
    }

    /**
     * Check if user is a global admin.
     * Global admins are users from the federation company (FBBA) with admin rights.
     * They can access both old and new applications regardless of migration status.
     * <p>
     * Note: Every user belongs to exactly one company - global admins are not an exception,
     * they are simply users from the FBBA company with admin privileges.
     */
    public boolean isGlobalAdmin() {
        boolean isAdmin = getGroupsStream()
            .anyMatch(g -> ADMIN_GROUP.equalsIgnoreCase(g.getName()));

        return isAdmin && getCompanyIdOptional()
            .map(FEDERATION_COMPANY_ID::equalsIgnoreCase)
            .orElse(false);
    }

    /**
     * Check if the user's company has been migrated to the new application.
     * Returns false if company is not set (safe default - user cannot access new app).
     */
    public boolean isCompanyMigrated() {
        return entity.getCompanyOptional()
            .map(FoodbankCompany::isMigrated)
            .orElse(false);
    }

    // ==================== Password Methods ====================

    public String getPassword() {
        return entity.getPassword();
    }

    public void setPassword(String password) {
        entity.setPassword(password);
    }

    // ==================== UserModel Implementation ====================

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        return entity.getId();
    }

    @Override
    public void setUsername(String username) {
        entity.setId(username);
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public void setEmail(String email) {
        entity.setEmail(email);
    }

    // ==================== Groups/Rights Methods ====================

    @Override
    protected Set<GroupModel> getGroupsInternal() {
        String rights = entity.getRights();
        if (rights == null || rights.isBlank()) {
            return Set.of();
        }

        logger.info("Groups is: " + rights);
        List<String> groupNames = Arrays.stream(rights.toLowerCase().split(" "))
            .filter(s -> !s.isBlank())
            .toList();

        Set<GroupModel> groupModelSet = realm.getGroupsStream()
            .filter(groupModel -> groupNames.contains(groupModel.getName().toLowerCase()))
            .collect(Collectors.toSet());

        logger.info("Result: " + groupModelSet.stream()
            .map(GroupModel::getName)
            .collect(Collectors.joining(", ")));

        return groupModelSet;
    }

    @Override
    public void joinGroup(GroupModel group) {
        if (!getGroupsInternal().contains(group)) {
            String rights = entity.getRights();
            String newRights = (rights == null || rights.isBlank())
                ? group.getName()
                : String.join(" ", rights, group.getName());
            entity.setRights(newRights);
        }
    }

    @Override
    public void leaveGroup(GroupModel group) {
        if (getGroupsInternal().contains(group)) {
            String rights = entity.getRights();
            if (rights != null) {
                String newRights = Arrays.stream(rights.split(" "))
                    .filter(right -> !right.equalsIgnoreCase(group.getName()))
                    .collect(Collectors.joining(" "));
                entity.setRights(newRights);
            }
        }
    }

    // ==================== Attributes ====================

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attrs = getFederatedStorage().getAttributes(realm, this.getId());
        if (attrs == null) {
            attrs = new MultivaluedHashMap<>();
        }

        String firstAndLastName = entity.getFirstAndLastName();
        if (firstAndLastName != null && !firstAndLastName.isBlank()) {
            String[] splitNames = firstAndLastName.split(" ", 2);
            attrs.add(UserModel.FIRST_NAME, splitNames[0]);
            attrs.add(UserModel.LAST_NAME, splitNames.length > 1 ? splitNames[1] : splitNames[0]);
        }

        attrs.add(UserModel.EMAIL, getEmail());
        attrs.add(UserModel.USERNAME, getUsername());

        return new MultivaluedHashMap<>(attrs);
    }
}
