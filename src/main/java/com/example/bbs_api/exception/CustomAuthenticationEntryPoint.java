package com.example.bbs_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Securityの認証失敗時に呼び出されるエントリーポイントの実装クラス。
 * <p>
 * 認証されていないリクエストに対し、HTTPステータス401 Unauthorizedを返し、
 * JSON形式のエラーレスポンスをクライアントに送信する。
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 認証エラー発生時に呼び出されるメソッド。
     * <p>
     * レスポンスのステータスコードを401に設定し、
     * Content-Typeをapplication/jsonにしてエラーメッセージをJSONで返す。
     *
     * @param request リクエスト情報
     * @param response レスポンス情報
     * @param authException 認証失敗の例外情報
     * @throws IOException 入出力エラー発生時
     * @throws ServletException サーブレット例外発生時
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // HTTPステータスコードを401 Unauthorizedに設定
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // Content-Typeをapplication/jsonに設定
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // レスポンスボディを作成
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase());

        // JSONに変換してレスポンスに書き込む
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
