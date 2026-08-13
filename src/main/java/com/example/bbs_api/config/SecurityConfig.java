package com.example.bbs_api.config;

import com.example.bbs_api.security.JwtAuthenticationFilter;
import com.example.bbs_api.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Securityの設定クラス。
 * <p>
 * このクラスは、アプリケーションのセキュリティポリシーを定義し、
 * JWT認証フィルターやCORS設定、認証マネージャーなどのBeanを登録します。
 * <p>
 * 主な設定内容:
 * <ul>
 *   <li>パスワードのハッシュ化にBCryptPasswordEncoderを使用</li>
 *   <li>ユーザー情報の取得にCustomUserDetailsServiceを利用し認証マネージャーを構築</li>
 *   <li>CORSを全てのオリジン・メソッド・ヘッダーで許可し、クレデンシャルは許可しない設定</li>
 *   <li>CSRFを無効化し、セッションはステートレスに設定（JWT認証用）</li>
 *   <li>APIの一部パス（/api/login, /api/public）は認証不要とし、その他はUSERロールを要求</li>
 *   <li>JWT認証フィルターをUsernamePasswordAuthenticationFilterの前に追加しトークン検証を行う</li>
 *   <li>認証失敗時の処理をAuthenticationEntryPointでカスタマイズ</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * コンストラクタ。
     *
     * @param userDetailsService ユーザー情報を提供するサービス
     * @param jwtAuthenticationFilter JWTの認証処理を行うフィルター
     * @param authenticationEntryPoint 認証エラー発生時のエントリーポイント
     */
    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * パスワードエンコーダーのBean定義。
     * <p>
     * BCryptアルゴリズムを用いてパスワードのハッシュ化を行う。
     *
     * @return PasswordEncoderのインスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 認証マネージャーのBean定義。
     * <p>
     * CustomUserDetailsServiceとパスワードエンコーダーを用いて認証処理を構築する。
     *
     * @param http HttpSecurityオブジェクト
     * @return AuthenticationManagerのインスタンス
     * @throws Exception 認証マネージャー構築時の例外
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    /**
     * CORS設定のBean定義。
     * <p>
     * 全てのオリジン・HTTPメソッド・ヘッダーを許可し、
     * クレデンシャルの送信は許可しない。
     * プリフライトリクエストのキャッシュは3600秒に設定。
     *
     * @return CorsConfigurationSourceのインスタンス
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * セキュリティフィルターチェーンのBean定義。
     * <p>
     * 以下の設定を行う:
     * <ul>
     *   <li>CSRF無効化</li>
     *   <li>CORS設定の適用</li>
     *   <li>ステートレスなセッション管理</li>
     *   <li>/api/loginと/api/publicは認証不要</li>
     *   <li>それ以外はUSERロールを要求</li>
     *   <li>JWT認証フィルターをUsernamePasswordAuthenticationFilterの前に挿入</li>
     *   <li>認証失敗時はauthenticationEntryPointを使用</li>
     * </ul>
     *
     * @param http HttpSecurityオブジェクト
     * @return SecurityFilterChainのインスタンス
     * @throws Exception 設定時の例外
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v*/login", "/api/public").permitAll()
                        .anyRequest().hasRole("USER"))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptions) -> {
                    exceptions.authenticationEntryPoint(authenticationEntryPoint);
                });

        return http.build();
    }
}
