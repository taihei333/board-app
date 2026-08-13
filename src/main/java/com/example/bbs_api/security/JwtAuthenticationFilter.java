package com.example.bbs_api.security;

import com.example.bbs_api.service.CustomUserDetailsService;
import com.example.bbs_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT認証用のフィルタークラス。
 * <p>
 * HTTPリクエストヘッダーからJWTトークンを抽出し、その有効性を検証することで、
 * 認証済みユーザーとしてSpring Securityのコンテキストに認証情報をセットする。
 * <p>
 * <b>処理の流れ:</b>
 * <ol>
 *   <li>Authorizationヘッダーから"Bearer "トークンを抽出</li>
 *   <li>JWTからユーザー名を取り出し、トークンの有効性を検証</li>
 *   <li>トークンが有効ならUserDetailsServiceでユーザー情報を取得</li>
 *   <li>UsernamePasswordAuthenticationTokenを作成し、SecurityContextにセット</li>
 *   <li>フィルターチェーンの次のフィルターに処理を渡す</li>
 * </ol>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * コンストラクタ。
     *
     * @param jwtUtil JWTトークン操作ユーティリティ
     * @param userDetailsService ユーザー詳細情報を提供するサービス
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * リクエスト毎に実行されるフィルター処理。
     * <p>
     * AuthorizationヘッダーからJWTを取得し、トークンの検証を行う。
     * トークンが有効かつ認証情報が設定されていなければ、
     * 認証オブジェクトを作成してSecurityContextにセットする。
     *
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param filterChain フィルターチェーン
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("JWT Token parsing error: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
