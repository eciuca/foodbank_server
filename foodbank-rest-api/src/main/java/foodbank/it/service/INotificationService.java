package foodbank.it.service;

import foodbank.it.persistence.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface INotificationService {
	 
		Optional<Notification> findByNotificationId(int notificationId);
		
		    
	    Notification save(Notification notification,boolean booCreateMode) throws Exception;

	    Page<Notification> findAll(SearchNotificationCriteria searchCriteria, Pageable pageable);	
	    

	    void delete(int notificationId) throws Exception;

}
