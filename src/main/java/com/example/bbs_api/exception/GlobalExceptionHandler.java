package com.example.bbs_api.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * アプリケーション全体で発生した例外を一元的に処理するグローバル例外ハンドラークラス。
 * <p>
 * 各種例外に応じて適切なHTTPステータスコードとエラーメッセージを
 * JSON形式でクライアントへ返却する。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * バリデーションエラー発生時に呼び出されるハンドラー。
     * <p>
     * リクエストボディのバリデーションエラーの詳細をフィールド単位で収集し、
     * 400 Bad Requestで返す。
     *
     * @param ex 発生したMethodArgumentNotValidException例外
     * @return エラーレスポンスを含むResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<FieldErrorDetail> detailList = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse("入力内容にエラーがあります。");
        errorResponse.setFieldErrors(detailList);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * メソッド引数の型不一致エラー発生時に呼び出されるハンドラー。
     * <p>
     * どの引数でエラーが発生したかの情報を収集し、400 Bad Requestで返す。
     *
     * @param ex 発生したMethodArgumentTypeMismatchException例外
     * @return エラーレスポンスを含むResponseEntity
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentTypeMismatchException ex) {

        List<FieldErrorDetail> detailList = Collections.singletonList(new FieldErrorDetail(ex.getName(), ex.getMessage()));

        ErrorResponse errorResponse = new ErrorResponse("入力内容にエラーがあります。");
        errorResponse.setFieldErrors(detailList);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * エンティティが見つからない場合に呼び出されるハンドラー。
     * <p>
     * 例えばデータベースの検索で該当データが存在しなかった場合のエラー処理。
     *
     * @param ex 発生したEntityNotFoundException例外
     * @return エラーレスポンスを含むResponseEntity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundExceptions(EntityNotFoundException ex) {

        List<FieldErrorDetail> detailList = Collections.singletonList(new FieldErrorDetail("body", ex.getMessage()));

        ErrorResponse errorResponse = new ErrorResponse("入力内容にエラーがあります。");
        errorResponse.setFieldErrors(detailList);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 権限拒否（アクセス禁止）エラー発生時に呼び出されるハンドラー。
     * <p>
     * 403 Forbiddenステータスでエラーメッセージを返す。
     *
     * @param ex 発生したAuthorizationDeniedException例外
     * @return エラーレスポンスを含むResponseEntity
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedExceptions(AuthorizationDeniedException ex) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 未処理のすべての例外を捕捉し、500 Internal Server Errorとして処理するハンドラー。
     * <p>
     * 発生した例外のスタックトレースを標準エラー出力に出力し、
     * 一般的なエラーメッセージをクライアントに返す。
     *
     * @param ex 発生した例外オブジェクト
     * @return エラーレスポンスを含むResponseEntity
     */
    @ExceptionHandler(Exception.class) // Exceptionクラスとそのサブクラスをすべて捕捉
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception ex) {
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
