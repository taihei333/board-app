package com.example.bbs_api.service;

import com.example.bbs_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring SecurityのUserDetailsServiceの実装クラス。
 * <p>
 * 指定されたメールアドレス（username）に対応するユーザー情報を
 * データベースから取得し、認証処理に必要なUserDetailsとして返却する。
 * <p>
 * ユーザーが見つからない場合はUsernameNotFoundExceptionをスローする。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * コンストラクタ。
     *
     * @param userRepository ユーザー情報の取得に使用するリポジトリ
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 指定されたメールアドレスに対応するユーザー情報を読み込む。
     *
     * @param email ユーザーのメールアドレス（usernameとして扱う）
     * @return UserDetails 認証に必要なユーザー情報
     * @throws UsernameNotFoundException 指定したメールアドレスのユーザーが存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
