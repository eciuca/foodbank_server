package foodbank.it.keycloak;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Optional;

/**
 * Entity representing a user in the system.
 * Maps to the t_user table.
 * <p>
 * Note: Every user belongs to exactly one company. The company relationship
 * is mandatory from a business perspective, even though the database allows null.
 */
@Getter
@NamedQueries({
    @NamedQuery(name = "getUserByUsername", query = "select u from FoodbankUser u left join fetch u.company where u.id = :username"),
    @NamedQuery(name = "getUserByEmail", query = "select u from FoodbankUser u left join fetch u.company where u.email = :email"),
    @NamedQuery(name = "getUserCount", query = "select count(u) from FoodbankUser u"),
    @NamedQuery(name = "getAllUsers", query = "select u from FoodbankUser u"),
    @NamedQuery(name = "searchForUser", query = "select u from FoodbankUser u where " +
        "( lower(u.id) like :search or lower(u.firstAndLastName) like :search or u.email like :search ) order by u.firstAndLastName"),
    @NamedQuery(name = "searchForGroupMembers", query = "select u from FoodbankUser u where u.rights like :search order by u.firstAndLastName")
})
@Entity
@Table(name = "t_user")
public class FoodbankUser {

    @Setter
    @Id
    @Column(name = "id_user")
    private String id;

    @Setter
    @Column(name = "user_name")
    private String firstAndLastName;

    @Setter
    @Column(name = "email")
    private String email;

    @Setter
    @Column(name = "password")
    private String password;

    @Setter
    @Column(name = "rights")
    private String rights;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_company", referencedColumnName = "id_company")
    private FoodbankCompany company;

    public Optional<FoodbankCompany> getCompanyOptional() {
        return Optional.ofNullable(company);
    }

}
