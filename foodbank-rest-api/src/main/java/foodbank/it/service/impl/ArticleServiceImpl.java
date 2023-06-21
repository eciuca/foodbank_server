package foodbank.it.service.impl;

import foodbank.it.persistence.model.Article;
import foodbank.it.persistence.repository.IArticleRepository;
import foodbank.it.service.IArticleService;
import foodbank.it.service.SearchArticleCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements IArticleService {

    private final IArticleRepository articleRepository;

    @Override
    public Optional<Article> findByArticleId(String articleId) {
        return articleRepository.findById(articleId);
    }

    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Page<Article> findAll(SearchArticleCriteria searchCriteria, Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public void delete(String articleId) throws Exception {
        findByArticleId(articleId).ifPresent(articleRepository::delete);
    }
}
