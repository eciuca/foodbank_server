package foodbank.it.service;

import foodbank.it.persistence.model.Article;
import foodbank.it.persistence.model.AuditAssoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IArticleService {
	
    Optional<Article> findByArticleId(String articleId);
	
	Article save(Article article) ;

    Page<Article> findAll(SearchArticleCriteria searchCriteria, Pageable pageable);
   

    void delete(String articleId) throws Exception;

}
