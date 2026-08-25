package com.example.bbs_api.controller;

import com.example.bbs_api.dto.JwtResponse;
import com.example.bbs_api.dto.LoginRequest;
import com.example.bbs_api.dto.SignupRequest;
import com.example.bbs_api.dto.SignupResponse;
import com.example.bbs_api.dto.UserResponse;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.exception.ErrorResponse;
import com.example.bbs_api.service.UserService;
import com.example.bbs_api.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

    /**
     * コンストラクタ。
     *
     * @param authenticationManager Spring Securityの認証マネージャー
     * @param jwtUtil JWTトークン生成・検証ユーティリティ
     * @param userService ユーザー登録などのビジネスロジックを提供するサービス
     */
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * 会員登録を行うエンドポイント。
     * <p>
     * リクエストボディで受け取った情報を元に新しいユーザーアカウントを作成する。
     * パスワードとパスワード確認が一致しない場合や、メールアドレスが既に
     * 登録済みの場合はバリデーションエラーとして400を返す。
     *
     * @param signupRequest 会員登録情報（email, name, password, passwordConfirmation）
     * @return 登録成功時は登録されたユーザーの詳細
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    /**
     * ログイン中のユーザー情報を取得するエンドポイント。
     * <p>
     * JWT認証フィルターによってセットされた認証情報から、
     * 現在ログイン中のユーザーのname・emailを返却する。
     *
     * @param authentication JWT認証フィルターがセットした認証情報
     * @return ログインユーザーのname・emailを含むレスポンス
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserResponse(user.getName(), user.getEmail()));
    }
}
