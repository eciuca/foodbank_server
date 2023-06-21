package foodbank.it.web.controller;

import foodbank.it.persistence.model.Article;
import foodbank.it.service.IArticleService;
import foodbank.it.web.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequiredArgsConstructor

public class ArticleController {

    private final IArticleService articleService;
    private final ArticleMapper articleMapper;

    @GetMapping("articles/{articleId}")
    public ArticleDto findOne(@PathVariable String articleId) {
        return articleService.findByArticleId(articleId)
                .map(articleMapper::map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @GetMapping("articles/")
    public Collection<ArticleDto> find(@RequestParam String offset, @RequestParam String rows,
                                       @RequestParam String sortField, @RequestParam String sortOrder,
                                       @RequestParam(required = false) String lienBanque, @RequestParam(required = false) String lienDep) {
        int intOffset = Integer.parseInt(offset);
        int intRows = Integer.parseInt(rows);
        int pageNumber = intOffset / intRows; // Java throws away remainder of division
        int pageSize = intRows;
        Pageable pageRequest = null;
        if (sortOrder.equals("1")) {
            pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortField).ascending());
        } else {
            pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortField).descending());
        }

        return articleService.findAll(null, pageRequest).stream()
                .map(articleMapper::map)
                .toList();

    }


    @PutMapping("articles/{articleId}")
    public ArticleDto updateAuditAssoc(@PathVariable("articleId") String articleId, @RequestBody ArticleDto updatedAuditAssoc) throws Exception {
        Article article = articleMapper.map(updatedAuditAssoc);
        article.setId(articleId);
        Article savedArticle = articleService.save(article);

        return articleMapper.map(savedArticle);

    }

    @DeleteMapping("articles/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable("articleId") String articleId) throws Exception {
        try {
            articleService.delete(articleId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("articles/")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto create(@RequestBody ArticleDto article) throws Exception {
        Article entity = articleMapper.map(article);
        Article createdArticle = this.articleService.save(entity);
        return articleMapper.map(createdArticle);
    }

}
