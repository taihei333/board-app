package com.example.bbs_api.controller;

import com.example.bbs_api.entity.User;
import com.example.bbs_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 会員登録API（POST /api/v1/signup）のエンドツーエンドテスト。
 * <p>
 * 実際のSpring Securityフィルターチェーン・バリデーション・
 * GlobalExceptionHandlerを通した振る舞いを検証する。
 * 各テストはトランザクションのロールバックによりDBへの影響を残さない。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerSignupTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String uniqueEmail() {
        return "signup-test-" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void 正しい情報で会員登録すると201とユーザー情報が返りDBに保存される() throws Exception {
        String email = uniqueEmail();
        String body = objectMapper.writeValueAsString(new SignupRequestBody(email, "山田太郎", "password123", "password123"));

        mockMvc.perform(post("/api/v1/signup")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("山田太郎"))
                .andExpect(jsonPath("$.email").value(email));

        User saved = userRepository.findByEmail(email).orElseThrow();
        assertThat(saved.getName()).isEqualTo("山田太郎");
        assertThat(passwordEncoder.matches("password123", saved.getPassword())).isTrue();
    }

    @Test
    void パスワードとパスワード確認が一致しない場合は400が返る() throws Exception {
        String email = uniqueEmail();
        String body = objectMapper.writeValueAsString(new SignupRequestBody(email, "山田太郎", "password123", "different456"));

        mockMvc.perform(post("/api/v1/signup")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field_errors[0].field").value("passwordConfirmation"));

        assertThat(userRepository.existsByEmail(email)).isFalse();
    }

    @Test
    void 既に登録済みのメールアドレスの場合は400が返る() throws Exception {
        String email = uniqueEmail();
        userRepository.save(new User("既存ユーザー", email, passwordEncoder.encode("password123")));

        String body = objectMapper.writeValueAsString(new SignupRequestBody(email, "山田太郎", "password123", "password123"));

        mockMvc.perform(post("/api/v1/signup")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field_errors[0].field").value("email"));
    }

    @Test
    void パスワードが短すぎる場合は400が返る() throws Exception {
        String email = uniqueEmail();
        String body = objectMapper.writeValueAsString(new SignupRequestBody(email, "山田太郎", "short1", "short1"));

        mockMvc.perform(post("/api/v1/signup")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 必須項目が空の場合は400が返る() throws Exception {
        String body = objectMapper.writeValueAsString(new SignupRequestBody("", "", "", ""));

        mockMvc.perform(post("/api/v1/signup")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    private record SignupRequestBody(String email, String name, String password, String passwordConfirmation) {
    }
}
