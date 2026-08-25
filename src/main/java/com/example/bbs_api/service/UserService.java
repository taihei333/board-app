package com.example.bbs_api.service;

import com.example.bbs_api.dto.SignupRequest;
import com.example.bbs_api.dto.SignupResponse;
import com.example.bbs_api.entity.User;
import com.example.bbs_api.exception.EmailAlreadyExistsException;
import com.example.bbs_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ユーザーに関するビジネスロジックを提供するサービスクラス。
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 新しいユーザーアカウントを登録する。
     *
     * @param request 会員登録リクエスト（email, name, password, passwordConfirmation）
     * @return 登録されたユーザーの詳細
     * @throws EmailAlreadyExistsException 指定されたメールアドレスが既に登録済みの場合
     */
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.email());
        }

        User user = new User(request.name(), request.email(), passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);

        return new SignupResponse(saved.getId(), saved.getName(), saved.getEmail());
    }
}
