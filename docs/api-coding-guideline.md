# API実装ガイドライン

`AuthController`（ログインAPI）を参照実装として、他のAPIも同じ構成・書き方で統一するための定義書。
新しいAPIを追加する際は、この文書のルールに沿って実装する。

---

## 1. レイヤー構成

1リソース（例: ログイン、投稿、コメント）につき、以下のパッケージに1つずつファイルを置く。

| レイヤー | パッケージ | 役割 |
|---|---|---|
| Controller | `controller` | リクエスト受付・レスポンス返却のみ。ビジネスロジックは持たない |
| DTO (Request) | `dto` | クライアントからの入力を受けるレコードクラス。バリデーション定義を持つ |
| DTO (Response) | `dto` | クライアントへ返す出力を表すレコードクラス |
| Entity | `entity` | DBテーブルに対応するJPAエンティティ |
| Repository | `repository` | `JpaRepository`を継承するインターフェース。DBアクセスのみ |
| Service | `service` | ビジネスロジック。Controllerから呼ばれる（必要な場合のみ作成） |
| Exception | `exception` | 例外クラス・エラーレスポンス・グローバルハンドラー |

参照: [AuthController.java](../src/main/java/com/example/bbs_api/controller/AuthController.java), [LoginRequest.java](../src/main/java/com/example/bbs_api/dto/LoginRequest.java), [JwtResponse.java](../src/main/java/com/example/bbs_api/dto/JwtResponse.java)

---

## 2. Controller の書き方

```java
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600)
public class XxxController {

    private final XxxRepository xxxRepository; // または Service

    public XxxController(XxxRepository xxxRepository) {
        this.xxxRepository = xxxRepository;
    }

    @PostMapping("/xxx")
    public ResponseEntity<?> doSomething(@Valid @RequestBody XxxRequest request) {
        try {
            // 処理
            return ResponseEntity.ok(new XxxResponse(...));
        } catch (SpecificException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: ..."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error: Internal Server Error"));
        }
    }
}
```

ルール:
- クラス先頭にJavadocで「何をするAPIか」を日本語で記述する。
- `@RestController` + `@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)` + `@CrossOrigin(origins = "*", maxAge = 3600)` を必ず付与する。
- 依存はコンストラクタインジェクション（`@Autowired`は使わない）。フィールドは`private final`。
- リクエストボディを受け取るメソッドには必ず `@Valid @RequestBody` を付ける。
- 戻り値は `ResponseEntity<?>`。成功時は `ResponseEntity.ok(...)`。
- 各メソッドにJavadoc（`@param`, `@return`）を書く。
- try-catchで想定される例外（`BadCredentialsException`など）を個別にキャッチし、最後に汎用`Exception`で500エラーを返す。ここで想定外の例外はメッセージを固定文言にする（詳細を漏らさない）。

---

## 3. Request DTO の書き方

`record`を使い、バリデーションアノテーションをフィールドに直接付ける。

```java
public record XxxRequest(
        @NotBlank(message = "Xxx is required")
        String xxx,

        @Email(message = "Email should be valid")
        String email
) {
}
```

- クラスにJavadocで各フィールドの意味・制約を記述（`@param`含む）。
- バリデーションメッセージは英語（既存の`LoginRequest`に合わせる）。

参照: [LoginRequest.java](../src/main/java/com/example/bbs_api/dto/LoginRequest.java)

---

## 4. Response DTO の書き方

同じく`record`。フィールド構成が固定パターンを持つ場合はオーバーロードコンストラクタで補完する（`JwtResponse`の`type`固定値のような例）。

```java
public record XxxResponse(
        Long id,
        String xxx
) {
}
```

参照: [JwtResponse.java](../src/main/java/com/example/bbs_api/dto/JwtResponse.java)

---

## 5. Entity の書き方

```java
@Entity
@Table(name = "xxxs")
public class Xxx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public Xxx() {}

    public Xxx(String name) {
        this.name = name;
    }

    // getter/setterを全フィールドに用意
}
```

- デフォルトコンストラクタ＋必須項目を受け取るコンストラクタの2種類を用意する。
- getter/setterは標準的なJavaBeans形式（`record`は使わない。JPAエンティティのため）。

参照: [User.java](../src/main/java/com/example/bbs_api/entity/User.java)

---

## 6. Repository の書き方

```java
@Repository
public interface XxxRepository extends JpaRepository<Xxx, Long> {

    /**
     * ...
     */
    Optional<Xxx> findByYyy(String yyy);
}
```

- `JpaRepository<Entity, ID型>`を継承するだけのインターフェース。
- メソッド名はSpring Data JPAの命名規則に従う（`findBy...`, `existsBy...`）。
- 各メソッドにJavadocを付ける。

参照: [UserRepository.java](../src/main/java/com/example/bbs_api/repository/UserRepository.java)

---

## 7. エラーハンドリング

新しいAPI固有の例外がある場合は、Controller内でtry-catchするか、`GlobalExceptionHandler`にハンドラーを追加する。

- 汎用的なエラー（バリデーション、型不一致、Not Found、権限拒否、想定外例外）は既存の`GlobalExceptionHandler`が処理するので、個別のController側で重複対応しない。
- レスポンスは必ず`ErrorResponse`（`message` + 任意で`fieldErrors`）を使う。JSONはスネークケースで返る（`@JsonNaming`設定済み）。
- 新しい例外クラスを追加する場合は`exception`パッケージに置き、`GlobalExceptionHandler`に`@ExceptionHandler`を追加する。

参照: [GlobalExceptionHandler.java](../src/main/java/com/example/bbs_api/exception/GlobalExceptionHandler.java), [ErrorResponse.java](../src/main/java/com/example/bbs_api/exception/ErrorResponse.java), [FieldErrorDetail.java](../src/main/java/com/example/bbs_api/exception/FieldErrorDetail.java)

---

## 8. Service を挟むかどうか

ログインAPIはControllerが直接`AuthenticationManager`を呼んでいるが、これは認証処理がSpring Security側に委譲されているための例外。

- DBアクセスやビジネスロジックが複雑な場合（例: 投稿の作成・更新、権限チェックを伴う処理）は`service`パッケージにServiceクラスを作り、ControllerからはServiceだけを呼ぶ。
- 単純なCRUDで済む場合はControllerから直接Repositoryを呼んでよい（過度な抽象化はしない）。

---

## 9. 認可設定（SecurityConfig）

新しいエンドポイントを追加したら、認証要否を`SecurityConfig`の`filterChain`に反映する。

- 認証不要にしたいパスは `requestMatchers(...)` に追加する。
- それ以外はデフォルトで `hasRole("USER")` が要求される。

参照: [SecurityConfig.java](../src/main/java/com/example/bbs_api/config/SecurityConfig.java)

---

## 10. 共通コーディングスタイル

- 全クラス・全publicメソッドに日本語Javadocを書く（概要 + `<p>` + 詳細 + `@param`/`@return`/`@throws`）。
- コメントは説明的な日本語コメント（処理の意図がJavadocで足りない場合のみ行コメントを追加）。
- パッケージは機能単位ではなくレイヤー単位（`controller`, `dto`, `entity`, `repository`, `service`, `exception`, `security`, `util`, `config`）で分ける。
- クラス名・メソッド名は英語、コメント・エラーメッセージ（クライアント向け）は日本語と英語が混在しているが、既存踏襲でOK（バリデーションメッセージ＝英語、業務エラーメッセージ＝日本語）。
