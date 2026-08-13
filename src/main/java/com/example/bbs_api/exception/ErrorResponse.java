package com.example.bbs_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

/**
 * APIのエラーレスポンスを表すクラス。
 * <p>
 * メッセージと、必要に応じて複数のフィールド単位のエラー詳細を保持する。
 * <p>
 * JSONシリアライズ時はスネークケース（snake_case）にフィールド名が変換され、
 * nullのフィールドはJSONに含めない設定となっている。
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * エラーメッセージの本文。
     */
    private final String message;

    /**
     * フィールド単位のエラー詳細リスト。
     * バリデーションエラーなど複数フィールドのエラーがある場合に設定される。
     */
    private List<FieldErrorDetail> fieldErrors;

    /**
     * メッセージのみを設定してエラー応答を生成するコンストラクタ。
     *
     * @param message エラーメッセージ
     */
    public ErrorResponse(String message) {
        this.message = message;
    }

    /**
     * エラーメッセージを取得する。
     *
     * @return エラーメッセージ文字列
     */
    public String getMessage() {
        return message;
    }

    /**
     * フィールド単位のエラー詳細リストを取得する。
     *
     * @return フィールドエラーのリスト（設定されていなければnull）
     */
    public List<FieldErrorDetail> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * フィールド単位のエラー詳細リストを設定する。
     *
     * @param fieldErrors フィールドエラーのリスト
     */
    public void setFieldErrors(List<FieldErrorDetail> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
