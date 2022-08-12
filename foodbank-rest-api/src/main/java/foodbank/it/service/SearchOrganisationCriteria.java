package foodbank.it.service;

public class SearchOrganisationCriteria {
	private Integer idDis;
	private Integer regId;
	private Integer classeFBBA;
	private String societe;
	private String adresse;
	private String nomDepot;
	private Integer lienBanque;
	private Integer lienDepot;
	private Boolean isDepot;
	private Boolean isFead;

	private String birbCode;
	private Boolean isAgreed;
	private Boolean actif;
	private String refInt;
	private Boolean cotAnnuelle;
	private Boolean cotSup;
	private String statut;
	private Boolean gestBen;
	private Boolean feadN;
	private String bankShortName;
	private Boolean hasLogins;
	
	
	public SearchOrganisationCriteria(Integer idDis,Integer regId, Integer classeFBBA, String societe, String adresse, Boolean isAgreed, Boolean actif, String nomDepot, 
			Integer lienBanque, Integer lienDepot, Boolean isDepot,Boolean isFead, String birbCode, String refInt,
			Boolean cotAnnuelle,Boolean cotSup,String statut,Boolean gestBen,Boolean feadN,String bankShortName,Boolean hasLogins) {
		this.idDis = idDis;
		this.regId = regId;
		this.classeFBBA = classeFBBA;
		this.societe = societe;
		this.adresse = adresse;
		this.isAgreed = isAgreed;
		this.nomDepot = nomDepot;		
		this.lienBanque = lienBanque;
		this.lienDepot = lienDepot;
		this.isDepot = isDepot;
		this.isFead = isFead;
		this.birbCode = birbCode;
		this.actif = actif;
		this.refInt = refInt;
		this.cotAnnuelle = cotAnnuelle;
		this.cotSup = cotSup;
		this.statut = statut;
		this.gestBen = gestBen;
		this.feadN = feadN;
		this.bankShortName = bankShortName;
		this.hasLogins = hasLogins;
	}

	

	public Integer getIdDis() {
		return idDis;
	}



	public void setIdDis(Integer idDis) {
		this.idDis = idDis;
	}



	public Integer getRegId() {
		return regId;
	}



	public void setRegId(Integer regId) {
		this.regId = regId;
	}



	public Integer getClasseFBBA() {
		return classeFBBA;
	}



	public void setClasseFBBA(Integer classeFBBA) {
		this.classeFBBA = classeFBBA;
	}



	public String getSociete() {
		return societe;
	}



	public void setSociete(String societe) {
		this.societe = societe;
	}



	public String getAdresse() {
		return adresse;
	}



	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}


	public Boolean getIsAgreed() {
		return isAgreed;
	}



	public void setIsAgreed(Boolean isAgreed) {
		this.isAgreed = isAgreed;
	}



	public Boolean getActif() {
		return actif;
	}



	public void setActif(Boolean actif) {
		this.actif = actif;
	}



	public String getNomDepot() {
		return nomDepot;
	}



	public void setNomDepot(String nomDepot) {
		this.nomDepot = nomDepot;
	}



	public Boolean getIsDepot() {
		return isDepot;
	}



	public void setIsDepot(Boolean isDepot) {
		this.isDepot = isDepot;
	}



	public Boolean getIsFead() {
		return isFead;
	}



	public void setIsFead(Boolean isFead) {
		this.isFead = isFead;
	}

	public String getBirbCode() {
		return birbCode;
	}

	public void setBirbCode(String birbCode) {
		this.birbCode = birbCode;
	}

	public String getRefInt() {
		return refInt;
	}



	public void setRefInt(String refInt) {
		this.refInt = refInt;
	}



	public Boolean getCotAnnuelle() {
		return cotAnnuelle;
	}



	public void setCotAnnuelle(Boolean cotAnnuelle) {
		this.cotAnnuelle = cotAnnuelle;
	}



	public Boolean getCotSup() {
		return cotSup;
	}



	public void setCotSup(Boolean cotSup) {
		this.cotSup = cotSup;
	}



	public String getStatut() {
		return statut;
	}



	public void setStatut(String statut) {
		this.statut = statut;
	}



	public Integer getLienBanque() {
		return lienBanque;
	}

	public void setLienBanque(Integer lienBanque) {
		this.lienBanque = lienBanque;
	}

	public Integer getlienDepot() {
		return lienDepot;
	}

	public void setlienDepot(Integer lienDepot) {
		this.lienDepot = lienDepot;
	}



	public Boolean getGestBen() {
		return gestBen;
	}



	public void setGestBen(Boolean gestBen) {
		this.gestBen = gestBen;
	}



	public Boolean getFeadN() {
		return feadN;
	}



	public void setFeadN(Boolean feadN) {
		this.feadN = feadN;
	}



	public String getBankShortName() {
		return bankShortName;
	}

	public void setBankShortName(String bankShortName) {
		this.bankShortName = bankShortName;
	}
	public Boolean getHasLogins() {
		return hasLogins;
	}

	public void setHasLogins(Boolean hasLogins) {
		this.hasLogins = hasLogins;
	}
	
}
