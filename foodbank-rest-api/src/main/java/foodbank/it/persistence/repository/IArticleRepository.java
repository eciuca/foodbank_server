package foodbank.it.persistence.repository;

import foodbank.it.persistence.model.Article;
import foodbank.it.persistence.model.AuditAssoc;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IArticleRepository extends PagingAndSortingRepository<Article, String>{
	
}
