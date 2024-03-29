package foodbank.it.web.dto;

import java.time.LocalDate;

public class PopulationReportDto {
	  private LocalDate dateStat;
	  private Integer lienBanque;
	  private Long nFam;
	  
	    private Long nPers;
	    
	    private Long nNour;
	  
	    private Long nBebe;
	    
	    private Long nEnf;
	    
	    private Long nAdo;
	   
	    private Long n1824;
	   
	   
	    private Long nSen;


		public PopulationReportDto(LocalDate dateStat, Integer lienBanque, Long nFam, Long nPers, Long nNour, Long nBebe,
				Long nEnf, Long nAdo, Long n1824, Long nSen) {
			super();
			this.dateStat = dateStat;
			this.lienBanque = lienBanque;
			this.nFam = nFam;
			this.nPers = nPers;
			this.nNour = nNour;
			this.nBebe = nBebe;
			this.nEnf = nEnf;
			this.nAdo = nAdo;
			this.n1824 = n1824;
			this.nSen = nSen;
		}


		


		public LocalDate getDateStat() {
			return dateStat;
		}





		public void setDateStat(LocalDate dateStat) {
			this.dateStat = dateStat;
		}





		public Integer getLienBanque() {
			return lienBanque;
		}





		public void setLienBanque(Integer lienBanque) {
			this.lienBanque = lienBanque;
		}





		public Long getnFam() {
			return nFam;
		}





		public void setnFam(Long nFam) {
			this.nFam = nFam;
		}





		public Long getnPers() {
			return nPers;
		}





		public void setnPers(Long nPers) {
			this.nPers = nPers;
		}





		public Long getnNour() {
			return nNour;
		}





		public void setnNour(Long nNour) {
			this.nNour = nNour;
		}





		public Long getnBebe() {
			return nBebe;
		}





		public void setnBebe(Long nBebe) {
			this.nBebe = nBebe;
		}





		public Long getnEnf() {
			return nEnf;
		}





		public void setnEnf(Long nEnf) {
			this.nEnf = nEnf;
		}





		public Long getnAdo() {
			return nAdo;
		}





		public void setnAdo(Long nAdo) {
			this.nAdo = nAdo;
		}





		public Long getN1824() {
			return n1824;
		}





		public void setN1824(Long n1824) {
			this.n1824 = n1824;
		}





		public Long getnSen() {
			return nSen;
		}





		public void setnSen(Long nSen) {
			this.nSen = nSen;
		}




}
