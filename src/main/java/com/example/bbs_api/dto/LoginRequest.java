package com.example.bbs_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ログインリクエストのデータを表すレコードクラス。
 * <p>
 * クライアントから受け取るログイン情報として、メールアドレスとパスワードを保持する。
 * 入力バリデーションとして、メールアドレスは必須かつ有効な形式であること、
 * パスワードは必須であることを定義している。
 *
 * @param email ユーザーのメールアドレス（必須・有効なメール形式）
 * @param password ユーザーのパスワード（必須）
 */
public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
