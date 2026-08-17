package com.example.bbs_api.controller;

import com.example.bbs_api.dto.ArticleCreateRequest;
import com.example.bbs_api.dto.ArticleDetailResponse;
import com.example.bbs_api.dto.ArticleListResponse;
import com.example.bbs_api.dto.ArticleResponse;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 記事関連のAPIを提供するコントローラークラス。
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * ページネーション・検索・ソート機能付きで記事一覧を取得するエンドポイント。
     *
     * @param page ページ番号（1始まり、デフォルト1）
     * @param size 1ページあたりの件数（デフォルト20）
     * @param title タイトルの部分一致検索（任意）
     * @param content 本文の部分一致検索（任意）
     * @param authorName 投稿者名の部分一致検索（任意）
     * @param sortBy ソート対象カラム（デフォルトcreatedAt）
     * @param order ソート順（デフォルトdesc）
     * @return 記事一覧とページネーション情報
     */
    @GetMapping("/articles")
    public ResponseEntity<?> getArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String authorName,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {

        ArticleListResponse response = articleService.findArticles(
                page, size, title, content, authorName, sortBy, order);

        return ResponseEntity.ok(response);
    }

    /**
     * 新しい記事を登録するエンドポイント。
     * <p>
     * ログイン中のユーザーを投稿者として記事を作成する。
     *
     * @param request 記事登録リクエスト（タイトル・本文）
     * @param authentication JWT認証フィルターがセットした認証情報
     * @return 作成された記事の詳細
     */
    @PostMapping("/articles")
    public ResponseEntity<?> createArticle(
            @Valid @RequestBody ArticleCreateRequest request,
            Authentication authentication) {

        User author = (User) authentication.getPrincipal();
        ArticleResponse response = articleService.createArticle(request, author);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 指定されたIDの記事詳細と、紐づくコメント一覧を取得するエンドポイント。
     *
     * @param articleId 記事ID
     * @return 記事詳細（コメント一覧を含む）
     */
    @GetMapping("/articles/{articleId}")
    public ResponseEntity<?> getArticleDetail(@PathVariable Long articleId) {
        ArticleDetailResponse response = articleService.getArticleDetail(articleId);
        return ResponseEntity.ok(response);
    }

    /**
     * 指定されたIDの記事を更新するエンドポイント。
     * <p>
     * 記事の投稿者本人のみが更新可能。
     *
     * @param articleId 記事ID
     * @param request 更新リクエスト（タイトル・本文）
     * @param authentication JWT認証フィルターがセットした認証情報
     * @return 更新後の記事詳細
     */
    @PutMapping("/articles/{articleId}")
    public ResponseEntity<?> updateArticle(
            @PathVariable Long articleId,
            @Valid @RequestBody ArticleCreateRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        ArticleResponse response = articleService.updateArticle(articleId, request, currentUser);

        return ResponseEntity.ok(response);
    }

    /**
     * 指定されたIDの記事を削除するエンドポイント。
     * <p>
     * 記事の投稿者本人のみが削除可能。
     *
     * @param articleId 記事ID
     * @param authentication JWT認証フィルターがセットした認証情報
     * @return 削除成功時は204 No Content（レスポンスボディなし）
     */
    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable Long articleId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        articleService.deleteArticle(articleId, currentUser);

        return ResponseEntity.noContent().build();
    }
}