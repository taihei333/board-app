package com.example.bbs_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 記事登録リクエストのデータを表すレコードクラス。
 *
 * @param title タイトル（必須・1〜50文字）
 * @param content 本文（必須・1〜10000文字）
 */
public record ArticleCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 50, message = "Title must be 50 characters or less")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 10000, message = "Content must be 10000 characters or less")
        String content
) {
}
