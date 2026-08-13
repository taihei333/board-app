package com.example.bbs_api.exception;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * フィールド単位のバリデーションエラー情報を表すレコードクラス。
 * <p>
 * JSONシリアライズ時にフィールド名はスネークケース（snake_case）に変換される。
 * <p>
 * フィールド:
 * <ul>
 *   <li>field: エラーが発生した対象のフィールド名</li>
 *   <li>message: エラーメッセージ</li>
 * </ul>
 *
 * @param field エラー対象のフィールド名
 * @param message エラーメッセージ
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FieldErrorDetail(String field, String message) {
}
