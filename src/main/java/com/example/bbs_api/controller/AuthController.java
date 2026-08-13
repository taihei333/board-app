package com.example.bbs_api.controller;

import com.example.bbs_api.dto.JwtResponse;
import com.example.bbs_api.dto.LoginRequest;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.exception.ErrorResponse;
import com.example.bbs_api.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 認証関連のAPIを提供するコントローラークラス。
 * <p>
 * 主にログインAPIを実装しており、クライアントからの認証情報を受け取り、
 * 認証処理を実行した上でJWTトークンを発行しレスポンスとして返却する。
 * <p>
 * エラー発生時は適切なエラーレスポンスを返す。
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * コンストラクタ。
     *
     * @param authenticationManager Spring Securityの認証マネージャー
     * @param jwtUtil JWTトークン生成・検証ユーティリティ
     */
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * ログイン認証を行うエンドポイント。
     * <p>
     * リクエストボディで受け取ったメールアドレスとパスワードを元に認証を試み、
     * 認証成功時はJWTトークンを生成し、ユーザーID・メールアドレスと共に返却する。
     * 認証失敗時はエラーメッセージを返す。
     *
     * @param loginRequest ログイン情報（メールアドレス・パスワード）
     * @return 認証成功時はJWTトークン等を含むレスポンス、失敗時はエラーレスポンス
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            User user = (User) userDetails;

            return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: Invalid credentials!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error: Internal Server Error"));
        }
    }
}
