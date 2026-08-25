package com.example.bbs_api.service;

import com.example.bbs_api.dto.CommentCreateRequest;
import com.example.bbs_api.dto.CommentCreateResponse;
import com.example.bbs_api.entity.Article;
import com.example.bbs_api.entity.Comment;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.exception.ResourceNotFoundException;
import com.example.bbs_api.repository.ArticleRepository;
import com.example.bbs_api.repository.CommentRepository;
import org.springframework.stereotype.Service;

/**
 * コメントに関するビジネスロジックを提供するサービスクラス。
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public CommentService(CommentRepository commentRepository, ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * 指定された記事にコメントを登録する。
     *
     * @param articleId コメント対象の記事ID
     * @param request コメント登録リクエスト（本文）
     * @param author コメント投稿者（ログイン中のユーザー）
     * @return 登録されたコメントの詳細
     * @throws ResourceNotFoundException 指定されたIDの記事が存在しない場合
     */
    public CommentCreateResponse createComment(Long articleId, CommentCreateRequest request, User author) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + articleId));

        Comment comment = new Comment(request.content(), article, author);
        Comment saved = commentRepository.save(comment);

        return new CommentCreateResponse(
                saved.getId(),
                article.getId(),
                author.getId(),
                author.getName(),
                saved.getContent(),
                saved.getCreatedAt());
    }
}