package foodbank.it.keycloak;

import lombok.Getter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a company (food bank) in the system.
 * Maps to the t_company table.
 */
@Getter
@Entity
@Table(name = "t_company")
public class FoodbankCompany {

    @Id
    @Column(name = "id_company")
    private String id;

    @Column(name = "migrated")
    private boolean migrated;

}
