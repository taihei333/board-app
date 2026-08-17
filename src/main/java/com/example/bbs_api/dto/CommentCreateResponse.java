package com.example.bbs_api.dto;

import java.time.OffsetDateTime;

/**
 * コメント登録APIのレスポンスを表すレコードクラス。
 *
 * @param id コメントID
 * @param articleId コメント対象の記事ID
 * @param authorId 投稿者ID
 * @param authorName 投稿者名
 * @param content コメント本文
 * @param createdAt 作成日時
 */
public record CommentCreateResponse(
        Long id,
        Long articleId,
        Long authorId,
        String authorName,
        String content,
        OffsetDateTime createdAt
) {
}