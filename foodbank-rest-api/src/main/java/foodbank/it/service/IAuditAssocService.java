package foodbank.it.service;

import java.util.Optional;

import foodbank.it.persistence.model.AuditAssoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAuditAssocService {
	
    Optional<AuditAssoc> findByAuditId(long auditAssocId);
	
	AuditAssoc save(AuditAssoc auditAssoc) ;

    Page<AuditAssoc> findAll(SearchAuditAssocCriteria searchCriteria, Pageable pageable);	
   

    void delete(long auditId) throws Exception;

}
