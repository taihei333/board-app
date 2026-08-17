package com.example.bbs_api.dto;

import java.util.List;

/**
 * 記事一覧取得APIのレスポンス全体を表すレコードクラス。
 *
 * @param data 記事一覧
 * @param pagination ページネーション情報
 */
public record ArticleListResponse(
        List<ArticleListItemResponse> data,
        PaginationResponse pagination
) {
}
