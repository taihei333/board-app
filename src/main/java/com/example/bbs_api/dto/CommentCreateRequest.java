package com.example.bbs_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * コメント登録リクエストのデータを表すレコードクラス。
 *
 * @param content コメント本文（必須・1〜1000文字）
 */
public record CommentCreateRequest(
        @NotBlank(message = "Content is required")
        @Size(max = 1000, message = "Content must be 1000 characters or less")
        String content
) {
}