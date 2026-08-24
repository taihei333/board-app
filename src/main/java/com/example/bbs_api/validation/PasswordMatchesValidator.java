package com.example.bbs_api.validation;

import com.example.bbs_api.dto.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link PasswordMatches} の検証ロジック。
 * <p>
 * SignupRequestのpasswordとpasswordConfirmationが一致するかを確認し、
 * 不一致の場合はpasswordConfirmationフィールドにエラーを紐づける。
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignupRequest> {

    @Override
    public boolean isValid(SignupRequest value, ConstraintValidatorContext context) {
        if (value == null || value.password() == null || value.passwordConfirmation() == null) {
            return true; // null/空チェックは @NotBlank 側に任せる
        }

        boolean matches = value.password().equals(value.passwordConfirmation());

        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("passwordConfirmation")
                    .addConstraintViolation();
        }

        return matches;
    }
}
