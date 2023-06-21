package foodbank.it.persistence.model;

import javax.persistence.*;

@Entity(name="cat_alimentaire")
public class FoodCategory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_article", unique=true, nullable=false, precision=19)
    private long id;

    @Column(name="nom_fr", length=30)
    private int frenchName;
    @Column(name="nom_nl", length=30)
    private int dutchName;
    @Column(name="nom_en", length=30)
    private int englishName;
    @Column(name="nom_ge", length=30)
    private int germanName;
}
