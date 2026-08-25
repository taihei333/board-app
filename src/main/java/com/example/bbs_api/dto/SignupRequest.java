package com.example.bbs_api.dto;

import com.example.bbs_api.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 会員登録リクエストのデータを表すレコードクラス。
 *
 * @param email ユーザーのメールアドレス（必須・有効なメール形式・最大255文字）
 * @param name ユーザー名（必須・最大10文字）
 * @param password パスワード（必須・8〜32文字・半角英数字のみ）
 * @param passwordConfirmation パスワード確認用（必須・passwordと一致すること）
 */
@PasswordMatches
public record SignupRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Size(max = 255, message = "Email must be 255 characters or less")
        String email,

        @NotBlank(message = "Name is required")
        @Size(max = 10, message = "Name must be 10 characters or less")
        String name,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Password must contain only letters and digits")
        String password,

        @NotBlank(message = "Password confirmation is required")
        String passwordConfirmation
) {
}
