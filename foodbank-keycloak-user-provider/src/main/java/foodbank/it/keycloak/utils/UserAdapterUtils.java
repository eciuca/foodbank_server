package foodbank.it.keycloak.utils;

import foodbank.it.keycloak.UserAdapter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;

import java.util.Optional;

/**
 * Utility class for working with UserAdapter instances.
 * Provides methods to safely extract UserAdapter from Keycloak's UserModel,
 * handling both cached and non-cached scenarios.
 */
public final class UserAdapterUtils {

    private UserAdapterUtils() {
        super();
    }

    /**
     * Extract UserAdapter from UserModel, handling cached users.
     * Returns empty if user is not from Foodbank provider.
     *
     * @param user the Keycloak UserModel
     * @return Optional containing UserAdapter if user is from Foodbank provider, empty otherwise
     */
    public static Optional<UserAdapter> fromUserModel(UserModel user) {
        if (user == null) {
            return Optional.empty();
        }

        if (user instanceof CachedUserModel) {
            CachedUserModel cachedUserModel = (CachedUserModel) user;
            UserModel delegate = cachedUserModel.getDelegateForUpdate();
            if (delegate instanceof UserAdapter) {
                UserAdapter adapter = (UserAdapter) delegate;
                return Optional.of(adapter);
            }
        } else if (user instanceof UserAdapter) {
            UserAdapter adapter = (UserAdapter) user;
            return Optional.of(adapter);
        }

        return Optional.empty();
    }

    /**
     * Extract UserAdapter from UserModel, throwing if not a Foodbank user.
     *
     * @param user the Keycloak UserModel
     * @return UserAdapter instance
     * @throws IllegalArgumentException if user is not from Foodbank provider
     */
    public static UserAdapter requireUserAdapter(UserModel user) {
        return fromUserModel(user)
            .orElseThrow(() -> new IllegalArgumentException(
                "User is not a Foodbank user: " + (user != null ? user.getUsername() : "null")));
    }
}
