package com.example.bbs_api.dto;

import java.time.OffsetDateTime;

/**
 * コメント1件分のレスポンスを表すレコードクラス。
 *
 * @param id コメントID
 * @param authorId 投稿者ID
 * @param authorName 投稿者名
 * @param content コメント本文
 * @param createdAt 作成日時
 */
public record CommentResponse(
        Long id,
        Long authorId,
        String authorName,
        String content,
        OffsetDateTime createdAt
) {
}