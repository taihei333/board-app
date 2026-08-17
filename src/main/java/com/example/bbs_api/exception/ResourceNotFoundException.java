package com.example.bbs_api.exception;

/**
 * 指定されたリソースが見つからない場合にスローされる例外クラス。
 * <p>
 * GlobalExceptionHandlerで捕捉され、404 Not Foundとして処理される。
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}