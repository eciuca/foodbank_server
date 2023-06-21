package foodbank.it.web.controller;

import foodbank.it.persistence.model.Article;
import foodbank.it.persistence.model.ArticleCategory;
import foodbank.it.persistence.model.FoodCategory;
import foodbank.it.web.dto.ArticleCategoryDto;
import foodbank.it.web.dto.ArticleDto;
import foodbank.it.web.dto.FoodCategoryDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ArticleMapper {

    ArticleDto map(Article article);
    Article map(ArticleDto article);

    FoodCategoryDto map(FoodCategory foodCategory);
    FoodCategory map(FoodCategoryDto foodCategory);

    ArticleCategoryDto map(ArticleCategory articleCategory);
    ArticleCategory map(ArticleCategoryDto articleCategory);
}
