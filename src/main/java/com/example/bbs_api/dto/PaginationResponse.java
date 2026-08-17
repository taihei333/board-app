package com.example.bbs_api.dto;

/**
 * ページネーション情報を表すレコードクラス。
 *
 * @param page 現在のページ番号（1始まり）
 * @param size 1ページあたりの件数
 * @param totalElements 全件数
 * @param totalPages 総ページ数
 */
public record PaginationResponse(
        int page,
        int size,
        long totalElements,
        int totalPages

) {
}
