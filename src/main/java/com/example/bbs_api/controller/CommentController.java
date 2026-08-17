package com.example.bbs_api.controller;

import com.example.bbs_api.dto.CommentCreateRequest;
import com.example.bbs_api.dto.CommentCreateResponse;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * コメント関連のAPIを提供するコントローラークラス。
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 指定された記事にコメントを登録するエンドポイント。
     *
     * @param articleId コメント対象の記事ID
     * @param request コメント登録リクエスト（本文）
     * @param authentication JWT認証フィルターがセットした認証情報
     * @return 作成されたコメントの詳細
     */
    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentCreateRequest request,
            Authentication authentication) {

        User author = (User) authentication.getPrincipal();
        CommentCreateResponse response = commentService.createComment(articleId, request, author);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}