package foodbank.it.persistence.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name="articles")
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_article", unique=true, nullable=false, precision=19)
    private String id;
    @Column(name="nom_fr", nullable=false, length=80)
    private int frenchName;
    @Column(name="nom_nl", nullable=false, length=80)
    private int dutchName;
    @ManyToOne
    @JoinColumn(name="id_cat_article")
    private ArticleCategory articleCategory;
    @ManyToOne
    @JoinColumn(name="id_cat_alimentaire")
    private FoodCategory foodCategory;
    @Column(name="id_cat_belge", length=10)
    private String belgeCategory;
    @Column(name = "delai")
    private Integer delai;
    @Column(name = "fead")
    private Integer fead;
    @Column(name = "anee_fead")
    private Integer aneeFead;
    @Column(name = "fead_pds_unit")
    private Integer feadPoidsUnit;
    @Column(name = "fead_ucart")
    private Integer feadUCart;
    @Column(name = "fead_cart_palette")
    private Integer feadCartPalete;
    @Column(name = "id_entreposage")
    private String idEntreposage;
    @Column(name = "fead_poids_brut_unite")
    private Integer feadPoidsUnitBrute;
    @Column(name = "index_order")
    private Integer indexOrder;
    @Column(name = "livre_banque")
    private Integer livreBanque;

}
