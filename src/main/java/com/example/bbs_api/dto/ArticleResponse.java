package com.example.bbs_api.dto;

import java.time.OffsetDateTime;

/**
 * 記事1件分の詳細を表すレコードクラス。
 * <p>
 * 記事登録・記事詳細取得・記事更新のレスポンスとして共通で使用する。
 *
 * @param id 記事ID
 * @param title タイトル
 * @param content 本文
 * @param authorId 投稿者ID
 * @param authorName 投稿者名
 * @param createdAt 作成日時
 * @param updatedAt 更新日時
 */
public record ArticleResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
