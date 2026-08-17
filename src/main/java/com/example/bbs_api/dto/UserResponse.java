package com.example.bbs_api.dto;

/**
 * ログインユーザー情報取得APIのレスポンスデータを表すレコードクラス。
 * <p>
 * フィールド:
 * <ul>
 *   <li>name: ユーザー名</li>
 *   <li>email: メールアドレス</li>
 * </ul>
 *
 * @param name ユーザー名
 * @param email メールアドレス
 */
public record UserResponse (
        String name,
        String email
){

}
