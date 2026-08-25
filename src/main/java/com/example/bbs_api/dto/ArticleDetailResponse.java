package com.example.bbs_api.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 記事詳細取得APIのレスポンスを表すレコードクラス。
 * <p>
 * 記事情報に加え、紐づくコメント一覧を保持する。
 *
 * @param id 記事ID
 * @param title タイトル
 * @param content 本文
 * @param authorId 投稿者ID
 * @param authorName 投稿者名
 * @param createdAt 作成日時
 * @param updatedAt 更新日時
 * @param comments コメント一覧
 */
public record ArticleDetailResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<CommentResponse> comments
) {
}