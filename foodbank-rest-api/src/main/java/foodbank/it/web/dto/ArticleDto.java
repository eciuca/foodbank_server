package foodbank.it.web.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class ArticleDto {
	private String id;
    private String frenchName;
    private String dutchName;
	private ArticleCategoryDto articleCategory;
	private FoodCategoryDto foodCategory;
    private String belgeCategory;
    private Integer delai;
    private Integer fead;
    private Integer aneeFead;
    private Integer feadPoidsUnit;
    private Integer feadUCart;
    private Integer feadCartPalete;
    private String idEntreposage;
    private Integer feadPoidsUnitBrute;
    private Integer indexOrder;
    private Integer livreBanque;

}


