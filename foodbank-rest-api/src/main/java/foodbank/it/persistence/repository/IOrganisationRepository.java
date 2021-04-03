package foodbank.it.persistence.repository;


import java.util.Optional;

import foodbank.it.persistence.model.Organisation;
import org.springframework.data.repository.CrudRepository;

public interface IOrganisationRepository extends CrudRepository<Organisation, Integer>{
    Optional<Organisation> findByIdDis(int idDis);
    void deleteByIdDis(int idDis);
    Iterable<Organisation> findByBanqueObjectBankShortName( String bankShortName);
}
