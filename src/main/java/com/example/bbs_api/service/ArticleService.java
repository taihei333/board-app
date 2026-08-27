package com.example.bbs_api.service;

import com.example.bbs_api.dto.*;
import com.example.bbs_api.entity.Article;
import com.example.bbs_api.entity.Comment;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.exception.ResourceNotFoundException;
import com.example.bbs_api.repository.ArticleRepository;
import com.example.bbs_api.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 記事に関するビジネスロジックを提供するサービスクラス。
 */
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 検索・ソート・ページネーション条件に基づき記事一覧を取得する。
     *
     * @param page ページ番号（1始まり）
     * @param size 1ページあたりの件数
     * @param title タイトルの部分一致検索キーワード（任意）
     * @param content 本文の部分一致検索キーワード（任意）
     * @param authorName 投稿者名の部分一致検索キーワード（任意）
     * @param sortBy ソート対象カラム（createdAt または title）
     * @param order ソート順（asc または desc）
     * @return 記事一覧とページネーション情報を含むレスポンス
     */

    @Transactional(readOnly = true)
    public ArticleListResponse findArticles(
            int page, int size, String title, String content, String authorName,
            String sortBy, String order) {

        Specification<Article> spec = buildSpecification(title, content, authorName);

        Sort sort = Sort.by("desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        Page<Article> articlePage = articleRepository.findAll(spec, pageRequest);

        List<ArticleListItemResponse> data = articlePage.getContent().stream()
                .map(a -> new ArticleListItemResponse(
                        a.getId(), a.getTitle(), a.getAuthor().getName(), a.getCreatedAt()))
                .toList();

        PaginationResponse pagination = new PaginationResponse(
                page, size, articlePage.getTotalElements(), articlePage.getTotalPages());

        return new ArticleListResponse(data, pagination);
    }

    private Specification<Article> buildSpecification(String title, String content, String authorName) {
        Specification<Article> spec = Specification.unrestricted();

        if (title != null && !title.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("title"), "%" + title + "%"));
        }
        if (content != null && !content.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("content"), "%" + content + "%"));
        }
        if (authorName != null && !authorName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("author").get("name"), "%" + authorName + "%"));
        }
        return spec;
    }

    /**
     * 新しい記事を登録する。
     *
     * @param request 記事登録リクエスト（タイトル・本文）
     * @param author 記事の投稿者（ログイン中のユーザー）
     * @return 登録された記事の詳細
     */
    public ArticleResponse createArticle(ArticleCreateRequest request, User author) {
        Article article = new Article(request.title(), request.content(), author);
        Article saved = articleRepository.save(article);

        return new ArticleResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getAuthor().getId(),
                saved.getAuthor().getName(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }

    /**
     * 指定されたIDの記事詳細と、紐づくコメント一覧を取得する。
     *
     * @param articleId 記事ID
     * @return 記事詳細（コメント一覧を含む）
     * @throws ResourceNotFoundException 指定されたIDの記事が存在しない場合
     */
    @Transactional(readOnly = true)
    public ArticleDetailResponse getArticleDetail(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + articleId));

        List<CommentResponse> comments = commentRepository.findByArticleIdOrderByCreatedAtAsc(articleId).stream()
                .map(c -> new CommentResponse(
                        c.getId(), c.getAuthor().getId(), c.getAuthor().getName(), c.getContent(), c.getCreatedAt()))
                .toList();

        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getAuthor().getId(),
                article.getAuthor().getName(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                comments);
    }

    /**
     * 指定されたIDの記事を更新する。
     * <p>
     * 記事の投稿者本人のみが更新可能。
     *
     * @param articleId 記事ID
     * @param request 更新リクエスト（タイトル・本文）
     * @param currentUser 現在ログイン中のユーザー
     * @return 更新後の記事詳細
     * @throws ResourceNotFoundException 指定されたIDの記事が存在しない場合
     * @throws AuthorizationDeniedException ログイン中のユーザーが投稿者本人でない場合
     */
    @Transactional
    public ArticleResponse updateArticle(Long articleId, ArticleCreateRequest request, User currentUser) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + articleId));

        if (!article.getAuthor().getId().equals(currentUser.getId())) {
            throw new AuthorizationDeniedException("You are not the author of this article");
        }

        article.setTitle(request.title());
        article.setContent(request.content());
        Article saved = articleRepository.save(article);

        return new ArticleResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getAuthor().getId(),
                saved.getAuthor().getName(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }

    /**
     * 指定されたIDの記事を削除する。
     * <p>
     * 記事の投稿者本人のみが削除可能。紐づくコメントも合わせて削除する。
     *
     * @param articleId 記事ID
     * @param currentUser 現在ログイン中のユーザー
     * @throws ResourceNotFoundException 指定されたIDの記事が存在しない場合
     * @throws AuthorizationDeniedException ログイン中のユーザーが投稿者本人でない場合
     */
    @Transactional
    public void deleteArticle(Long articleId, User currentUser) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + articleId));

        if (!article.getAuthor().getId().equals(currentUser.getId())) {
            throw new AuthorizationDeniedException("You are not the author of this article");
        }

        List<Comment> comments = commentRepository.findByArticleIdOrderByCreatedAtAsc(articleId);
        commentRepository.deleteAll(comments);

        articleRepository.delete(article);
    }

}
