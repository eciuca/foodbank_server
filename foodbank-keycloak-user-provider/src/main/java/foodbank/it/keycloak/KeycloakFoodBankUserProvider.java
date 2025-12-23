package foodbank.it.keycloak;

import at.favre.lib.crypto.bcrypt.BCrypt;
import foodbank.it.keycloak.utils.UserAdapterUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import javax.ejb.Remove;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class KeycloakFoodBankUserProvider implements
    UserStorageProvider,
    UserLookupProvider,
    UserRegistrationProvider,
    UserQueryProvider,
    CredentialInputUpdater,
    CredentialInputValidator,
    OnUserCache {

    public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";
    private static final BCrypt.Hasher HASHER = BCrypt.with(BCrypt.Version.VERSION_2Y);
    public static final int HASH_COST = 10;

	/* Password must contain at least one digit [0-9].
	Password must contain at least one lowercase Latin character [a-z].
	Password must contain at least one uppercase Latin character [A-Z].
	Password must contain a length of at least 8 characters and a maximum of 20 characters.
	*/

    protected final EntityManager entityManager;
    protected final ComponentModel model;
    protected final KeycloakSession session;

    @Remove
    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        log.info("getUserById: {}", id);
        String persistenceId = StorageId.externalId(id);
        FoodbankUser entity = entityManager.find(FoodbankUser.class, persistenceId);
        if (entity == null) {
            log.info("could not find user by id: {}", id);
            return null;
        }
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        try {
            log.info("getUserByUsername: {}", username);
            TypedQuery<FoodbankUser> query = entityManager.createNamedQuery("getUserByUsername", FoodbankUser.class);
            query.setParameter("username", username);
            List<FoodbankUser> result = query.getResultList();
            if (result.isEmpty()) {
                log.info("could not find username: {}", username);
                return null;
            }

            return new UserAdapter(session, realm, model, result.get(0));
        } catch (Exception ex) {
            log.error("Error getting user by username: {}", username, ex);
            throw ex;
        }
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        TypedQuery<FoodbankUser> query = entityManager.createNamedQuery("getUserByEmail", FoodbankUser.class);
        query.setParameter("email", email);
        List<FoodbankUser> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return new UserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        FoodbankUser entity = new FoodbankUser();
        entity.setId(UUID.randomUUID().toString());
        entity.setFirstAndLastName(username);
        entityManager.persist(entity);
        log.info("added user: {}", username);
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        String persistenceId = StorageId.externalId(user.getId());
        FoodbankUser entity = entityManager.find(FoodbankUser.class, persistenceId);
        if (entity == null) {
            return false;
        }
        entityManager.remove(entity);
        return true;
    }

    @Override
    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
        String password = ((UserAdapter) delegate).getPassword();
        if (password != null) {
            user.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserAdapter adapter = getUserAdapter(user);
        UserCredentialModel cred = (UserCredentialModel) input;
        PasswordPolicyManagerProvider provider = session.getProvider(PasswordPolicyManagerProvider.class);
        String password = cred.getValue();

        PolicyError policyError = provider.validate(realm, user, password);

        if (policyError == null) {
            log.info("Password changed for user: {} to {}", user.getUsername(), password);
            String hashedPassword = HASHER.hashToString(HASH_COST, password.toCharArray());
            adapter.setPassword(hashedPassword);

            return true;
        }

        return false;
    }

    public UserAdapter getUserAdapter(UserModel user) {
        return UserAdapterUtils.requireUserAdapter(user);
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return;
        }

        getUserAdapter(user).setPassword(null);
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        if (getUserAdapter(user).getPassword() == null) {
            return Stream.of();
        }

        return Stream.of(PasswordCredentialModel.TYPE);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType) && getPassword(user) != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.info("Validating user ...");
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel cred = (UserCredentialModel) input;
        String password = getPassword(user);
        String credentialValue = cred.getValue();

        BCrypt.Result verify = BCrypt.verifyer().verify(credentialValue.toCharArray(), password.toCharArray());
        return verify.verified;
    }

    public String getPassword(UserModel user) {
        String password = null;
        if (user instanceof CachedUserModel) {
            password = (String) ((CachedUserModel) user).getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (user instanceof UserAdapter) {
            password = ((UserAdapter) user).getPassword();
        }
        return password;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        Object count = entityManager.createNamedQuery("getUserCount")
            .getSingleResult();
        return ((Number) count).intValue();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        String usernameSearch = params.get(UserModel.SEARCH);
        log.info("Searching for user {}", usernameSearch);
        TypedQuery<FoodbankUser> query = entityManager.createNamedQuery("searchForUser", FoodbankUser.class);
        query.setParameter("search", "%" + usernameSearch.toLowerCase() + "%");
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList()
            .stream()
            .map(foodbankUser -> new UserAdapter(session, realm, model, foodbankUser));
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        log.info("Get group members {}", group.getName());
        String groupNameLike = "%" + group.getName() + "%";
        TypedQuery<FoodbankUser> query = entityManager.createNamedQuery("searchForGroupMembers", FoodbankUser.class);
        query.setParameter("search", groupNameLike);
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList()
            .stream()
            .map(foodbankUser -> new UserAdapter(session, realm, model, foodbankUser));
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.of();
    }
}
