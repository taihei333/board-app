package com.example.bbs_api.dto;

import java.time.OffsetDateTime;

/**
 * 記事一覧の1件分を表すレコードクラス。
 *
 * @param id 記事ID
 * @param title タイトル
 * @param authorName 投稿者名
 * @param createdAt 作成日時
 */
public record ArticleListItemResponse(
        Long id,
        String title,
        String authorName,
        OffsetDateTime createdAt
) {
}
