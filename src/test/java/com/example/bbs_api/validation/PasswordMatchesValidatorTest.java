package com.example.bbs_api.validation;

import com.example.bbs_api.dto.SignupRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link PasswordMatchesValidator} の単体テスト。
 * <p>
 * SpringコンテキストなしでBean Validationのみを使って検証する。
 */
class PasswordMatchesValidatorTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void passwordとpasswordConfirmationが一致する場合は違反がない() {
        SignupRequest request = new SignupRequest(
                "user@example.com", "山田太郎", "password123", "password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void passwordとpasswordConfirmationが一致しない場合はpasswordConfirmationに違反が付く() {
        SignupRequest request = new SignupRequest(
                "user@example.com", "山田太郎", "password123", "different456");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<SignupRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("passwordConfirmation");
        assertThat(violation.getMessage()).isEqualTo("Password confirmation does not match");
    }

    @Test
    void passwordConfirmationが空文字の場合はNotBlankとPasswordMatchesの両方に違反する() {
        SignupRequest request = new SignupRequest(
                "user@example.com", "山田太郎", "password123", "");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("passwordConfirmation"));
    }
}
