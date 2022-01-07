package foodbank.it.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import foodbank.it.persistence.model.Organisation;
import foodbank.it.persistence.model.TUser;
import foodbank.it.service.IMailService;
import foodbank.it.service.SearchMailListCriteria;
import foodbank.it.web.dto.MailAddressDto;

@Service
public class MailServiceImpl implements IMailService{

	
	private final EntityManager entityManager;
	
	public MailServiceImpl(EntityManager entityManager) {
         this.entityManager = entityManager;
    }
	@Override
	public List<MailAddressDto> find(SearchMailListCriteria 
			searchCriteria) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Organisation> organisationQuery = criteriaBuilder.createQuery(Organisation.class);
		Root<Organisation> organisation = organisationQuery.from(Organisation.class);

		List<Predicate> predicates = new ArrayList<>();

		
		Integer lienBanque = searchCriteria.getLienBanque();
		Integer lienDis = searchCriteria.getLienDis();
		Integer regId =  searchCriteria.getRegId();
		Boolean feadN = searchCriteria.getFeadN();
		Boolean isAgreed = searchCriteria.getAgreed();
		String target = searchCriteria.getTarget();
		
		if (lienBanque != null) {
			Predicate lienBanquePredicate = criteriaBuilder.equal(organisation.get("lienBanque"), lienBanque);
			predicates.add(lienBanquePredicate);
		}
		

		if (lienDis != null) {
			    Predicate lienDisPredicate = criteriaBuilder.equal(organisation.get("idDis"), lienDis);
				predicates.add(lienDisPredicate);
	    }
		
		if (regId != null) {
		    Predicate regIdPredicate = criteriaBuilder.equal(organisation.get("region"), regId	);
			predicates.add(regIdPredicate);
		}
		if (feadN != null) {
			Integer intfeadN = 0;
			if (feadN == true) {
				intfeadN = 1;
			}
			Predicate isfeadNPredicate = criteriaBuilder.equal(organisation.get("feadN"), intfeadN);
			predicates.add(isfeadNPredicate);
		}

		if (isAgreed != null) {
			// Note daten field means is reverse of Agreed
			Integer intDaten = 1;
			if (isAgreed == true) {
				intDaten = 0;
			}
			Predicate isAgreedPredicate = criteriaBuilder.equal(organisation.get("daten"), intDaten);
			predicates.add(isAgreedPredicate);
		}
	
		
		Predicate lienActifPredicate = criteriaBuilder.equal(organisation.get("actif"),1);
		predicates.add(lienActifPredicate);

		organisationQuery.where(predicates.stream().toArray(Predicate[]::new));	
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(criteriaBuilder.asc(organisation.get("idDis")));
		
		organisationQuery.orderBy(orderList);

		TypedQuery<Organisation> query = entityManager.createQuery(organisationQuery);
		List<Organisation> selectedOrganisations = query.getResultList();
		if (target == null) {
			return selectedOrganisations.stream()
				.map(org -> convertOrganisationToMailAddressContactDto(org))
				.collect(Collectors.toList());
		} else {
			List<MailAddressDto> returnedDtos = new ArrayList<MailAddressDto>();
			Iterator<Organisation> iterator = selectedOrganisations.iterator();
			
			//simple iteration
			while(iterator.hasNext()){
				Organisation org = iterator.next();
				returnedDtos.addAll(convertOrganisationToMailAddressPersonDto(org,target));
			}
			return returnedDtos;			
		}
	}
	protected MailAddressDto convertOrganisationToMailAddressContactDto(Organisation org) {  
    	   	
    	MailAddressDto dto = new MailAddressDto(org.getIdDis() + " " + org.getSociete(),"Organisation","Contact",org.getEmail());
    	return dto;
	}
	protected List<MailAddressDto> convertOrganisationToMailAddressPersonDto(Organisation org,String target) {  
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<TUser> tuserQuery = criteriaBuilder.createQuery(TUser.class);
		Root<TUser> tuser = tuserQuery.from(TUser.class);		
		List<Predicate> predicates = new ArrayList<>();
		Predicate idOrgPredicate = criteriaBuilder.equal(tuser.get("idOrg"), org.getIdDis());
		predicates.add(idOrgPredicate);	
		Predicate isActifPredicate = criteriaBuilder.equal(tuser.get("actif"), 1);
		predicates.add(isActifPredicate);
		Predicate rightsPredicate = criteriaBuilder.equal(tuser.get("rights"), "Admin_Asso");
		predicates.add(rightsPredicate);
		tuserQuery.where(predicates.stream().toArray(Predicate[]::new));
		tuserQuery.orderBy(criteriaBuilder.asc(tuser.get("userName")));

		TypedQuery<TUser> query = entityManager.createQuery(tuserQuery);
		List<TUser> selectedUsers = query.getResultList();
	   	
		return selectedUsers.stream()
				.map(user -> convertUserToMailAddress(user, org))
				.collect(Collectors.toList());
	}
	protected MailAddressDto convertUserToMailAddress(TUser user,Organisation org) {
		MailAddressDto dto = new MailAddressDto(org.getIdDis() + " " + org.getSociete(),user.getMembreNom(),
				user.getMembrePrenom(),user.getEmail());
		return dto;
	}
}
