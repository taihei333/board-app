package com.example.bbs_api.exception;

/**
 * 会員登録時に、指定されたメールアドレスが既に登録済みの場合にスローされる例外クラス。
 * <p>
 * GlobalExceptionHandlerで捕捉され、400 Bad Requestとして処理される。
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
