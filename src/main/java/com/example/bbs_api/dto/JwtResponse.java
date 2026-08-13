package com.example.bbs_api.dto;

/**
 * JWT認証成功時にクライアントへ返却するレスポンスデータを表すレコードクラス。
 * <p>
 * フィールド:
 * <ul>
 *   <li>token: 発行されたJWTトークン</li>
 *   <li>type: トークンタイプ（通常は "Bearer"）</li>
 *   <li>id: 認証ユーザーのID</li>
 *   <li>email: 認証ユーザーのメールアドレス</li>
 * </ul>
 */
public record JwtResponse(
        String token,
        String type,
        Long id,
        String email
) {
    /**
     * トークンタイプを "Bearer" に固定したコンストラクタ。
     *
     * @param token JWTトークン
     * @param id 認証ユーザーのID
     * @param email 認証ユーザーのメールアドレス
     */
    public JwtResponse(String token, Long id, String email) {
        this(token, "Bearer", id, email);
    }
}
