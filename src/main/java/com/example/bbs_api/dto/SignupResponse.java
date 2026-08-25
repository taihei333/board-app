package com.example.bbs_api.dto;

/**
 * 会員登録APIのレスポンスデータを表すレコードクラス。
 *
 * @param id 登録されたユーザーID
 * @param name ユーザー名
 * @param email メールアドレス
 */
public record SignupResponse(
        Long id,
        String name,
        String email
) {
}
