# APIテスト仕様書

対象: 全7API（ログインユーザー情報取得、記事一覧取得、記事登録、記事詳細取得、記事更新、記事削除、コメント登録）

参考: [5 Best Practices for REST API Testing](https://apidog.com/jp/blog/5-best-practices-for-rest-api-testing-2-jp/) の5つの観点をもとに、各APIのテストケースを設計する。

| # | 観点 | 本仕様書での扱い |
|---|---|---|
| 1 | CRUD操作の徹底的なテスト | 各APIの正常系・異常系ケースとして網羅 |
| 2 | HTTPステータスコード検証 | 各ケースに期待ステータスコードを明記 |
| 3 | セキュアコーディング | 認証・認可・入力エスケープの観点でケース化 |
| 4 | 入力バリデーション | 必須項目・文字数・型不一致のケースを網羅 |
| 5 | 負荷・パフォーマンステスト | 本仕様書の対象外（別途、JMeter/k6等での負荷試験計画を検討） |

---

## 共通のテスト観点（全API共通チェックリスト）

- [ ] 正常系：仕様通りのレスポンスボディ・ステータスコードが返る
- [ ] 認証なし：`Authorization`ヘッダーなしで`401`が返る（`/login`を除く）
- [ ] 認証あり・無効トークン：期限切れ/改ざんされたJWTで`401`が返る
- [ ] バリデーションエラー：必須項目欠落・文字数超過で`400`が返り、`field_errors`にフィールド名が含まれる
- [ ] 存在しないリソースID：`404`が返る（該当APIのみ）
- [ ] 他人のリソースへの操作：`403`が返る（更新・削除APIのみ）
- [ ] 想定外エラー：内部例外発生時に`500`が返り、詳細情報が漏れていない

---

## 1. ログインAPI（`POST /api/v1/login`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| LOGIN-01 | CRUD/正常系 | 登録済みユーザーが存在 | 正しいemail・password | `200`、`token`/`id`/`email`を含むJSON |
| LOGIN-02 | ステータスコード | 登録済みユーザーが存在 | 誤ったpassword | `400`、`Error: Invalid credentials!` |
| LOGIN-03 | 入力バリデーション | - | emailが空文字 | `400`、`field_errors`に`email`が含まれる |
| LOGIN-04 | 入力バリデーション | - | emailが不正な形式（例: `abc`） | `400`、`Email should be valid` |
| LOGIN-05 | 入力バリデーション | - | passwordが空文字 | `400`、`field_errors`に`password`が含まれる |
| LOGIN-06 | セキュア | - | 存在しないemailでログイン試行 | `400`（存在しないことが分かるメッセージになっていないか確認＝ユーザー列挙対策） |
| LOGIN-07 | セキュア | - | SQLインジェクション文字列（`' OR '1'='1`）をemailに入力 | `400`（バリデーションエラー or 認証失敗。SQLが実行されないこと） |

---

## 2. ログインユーザー情報取得API（`GET /api/v1/me`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| ME-01 | CRUD/正常系 | 有効なJWTを保持 | Authorizationヘッダーあり | `200`、`name`/`email`が一致 |
| ME-02 | ステータスコード | - | Authorizationヘッダーなし | `401` |
| ME-03 | ステータスコード | - | 無効なJWT（改ざん・期限切れ） | `401` |
| ME-04 | セキュア | - | 他ユーザーのIDを推測してリクエスト | JWTに紐づくユーザー本人の情報のみ返ること（IDOR対策の確認） |

---

## 3. 記事一覧取得API（`GET /api/v1/articles`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| ARTLIST-01 | CRUD/正常系 | 記事が複数件存在 | パラメータなし | `200`、`page=1`, `size=20`のデフォルト値でページング |
| ARTLIST-02 | CRUD/正常系 | 記事が3件存在 | `page=1&size=2` | `200`、`data`が2件、`pagination.totalElements=3` |
| ARTLIST-03 | CRUD/正常系 | タイトルに"猫"を含む記事が1件 | `title=猫` | `200`、該当記事のみ返る |
| ARTLIST-04 | CRUD/正常系 | 投稿者名"山田"の記事が存在 | `authorName=山田` | `200`、該当記事のみ返る |
| ARTLIST-05 | CRUD/正常系 | - | `sortBy=title&order=asc` | `200`、タイトル昇順で返る |
| ARTLIST-06 | 入力バリデーション | - | `size=101`（上限超過） | `400` |
| ARTLIST-07 | 入力バリデーション | - | `page=abc`（型不一致） | `400`（`MethodArgumentTypeMismatchException`） |
| ARTLIST-08 | ステータスコード | - | Authorizationヘッダーなし | `401` |
| ARTLIST-09 | CRUD/境界値 | 記事が0件 | パラメータなし | `200`、`data=[]`、`totalElements=0` |
| ARTLIST-10 | セキュア | - | `title`にXSS文字列（`<script>alert(1)</script>`）を入力 | `200`、LIKE検索としてそのまま扱われ、スクリプトが実行されないこと（レスポンスのエスケープ確認） |

---

## 4. 記事登録API（`POST /api/v1/articles`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| ARTCREATE-01 | CRUD/正常系 | 認証済み | 正しいtitle・content | `201`、登録内容と一致するレスポンス、DBに保存されている |
| ARTCREATE-02 | 入力バリデーション | - | titleが空文字 | `400`、`field_errors`に`title` |
| ARTCREATE-03 | 入力バリデーション | - | titleが51文字 | `400` |
| ARTCREATE-04 | 入力バリデーション | - | contentが空文字 | `400` |
| ARTCREATE-05 | 入力バリデーション | - | contentが10001文字 | `400` |
| ARTCREATE-06 | ステータスコード | - | Authorizationヘッダーなし | `401` |
| ARTCREATE-07 | CRUD | 登録後 | 作成した記事を`GET /articles/{id}`で取得 | 登録時と同じ内容が取得できる |
| ARTCREATE-08 | セキュア | - | titleにHTMLタグ（`<b>test</b>`）を含めて登録 | 保存も取得も文字列としてそのまま扱われ、レスポンス側でエスケープされること（保存側でスクリプト実行等が起きないこと） |

---

## 5. 記事詳細取得API（`GET /api/v1/articles/{articleId}`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| ARTDETAIL-01 | CRUD/正常系 | コメント2件付きの記事が存在 | 存在するarticleId | `200`、記事詳細＋`comments`が2件、作成日時昇順 |
| ARTDETAIL-02 | CRUD/正常系 | コメントなしの記事が存在 | 存在するarticleId | `200`、`comments=[]` |
| ARTDETAIL-03 | ステータスコード | - | 存在しないarticleId | `404` |
| ARTDETAIL-04 | 入力バリデーション | - | articleIdが数値でない（例: `abc`） | `400` |
| ARTDETAIL-05 | ステータスコード | - | Authorizationヘッダーなし | `401` |

---

## 6. 記事更新API（`PUT /api/v1/articles/{articleId}`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| ARTUPDATE-01 | CRUD/正常系 | 自分の記事が存在 | 正しいtitle・content | `200`、更新内容が反映、`updatedAt`が更新される |
| ARTUPDATE-02 | ステータスコード | 他人の記事が存在 | 正しいtitle・content | `403` |
| ARTUPDATE-03 | ステータスコード | - | 存在しないarticleId | `404` |
| ARTUPDATE-04 | 入力バリデーション | 自分の記事が存在 | titleが空文字 | `400` |
| ARTUPDATE-05 | ステータスコード | - | Authorizationヘッダーなし | `401` |
| ARTUPDATE-06 | CRUD | 更新後 | 更新した記事を再取得 | 更新後の内容が反映されている（`createdAt`は変わらない） |
| ARTUPDATE-07 | セキュア/IDOR | 他人の記事のIDを直接指定 | 正しいtitle・content | `403`（他人のデータを書き換えられないこと） |

---

## 7. 記事削除API（`DELETE /api/v1/articles/{articleId}`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| ARTDELETE-01 | CRUD/正常系 | 自分の記事（コメント付き）が存在 | 存在するarticleId | `204`、記事・紐づくコメントともに削除される |
| ARTDELETE-02 | ステータスコード | 他人の記事が存在 | 存在するarticleId | `403` |
| ARTDELETE-03 | ステータスコード | - | 存在しないarticleId | `404` |
| ARTDELETE-04 | ステータスコード | - | Authorizationヘッダーなし | `401` |
| ARTDELETE-05 | CRUD | 削除後 | 削除した記事を`GET /articles/{id}`で取得 | `404`が返る（削除が反映されている） |
| ARTDELETE-06 | セキュア/IDOR | 他人の記事のIDを直接指定 | - | `403`（他人のデータを削除できないこと） |

---

## 8. コメント登録API（`POST /api/v1/articles/{articleId}/comments`）

| ID | 観点 | 前提条件 | 入力 | 期待結果 |
|---|---|---|---|---|
| COMMENT-01 | CRUD/正常系 | 記事が存在 | 正しいcontent | `201`、コメント内容が一致するレスポンス |
| COMMENT-02 | ステータスコード | - | 存在しないarticleId | `404` |
| COMMENT-03 | 入力バリデーション | 記事が存在 | contentが空文字 | `400` |
| COMMENT-04 | 入力バリデーション | 記事が存在 | contentが1001文字 | `400` |
| COMMENT-05 | ステータスコード | - | Authorizationヘッダーなし | `401` |
| COMMENT-06 | CRUD | コメント登録後 | 対象記事を`GET /articles/{id}`で取得 | `comments`に登録したコメントが含まれる |
| COMMENT-07 | CRUD/権限 | 他人の記事が存在 | 正しいcontent | `201`（コメントは投稿者以外でも可能な仕様であることの確認） |

---

## 負荷・パフォーマンステスト（対象外・別途計画）

Apidogの記事にある5番目の観点（負荷・パフォーマンス）は、本仕様書のCRUD／単体レベルのテストとは性質が異なるため対象外としている。実施する場合は以下を別途計画する。

- ツール例: k6 / JMeter / Gatling
- 想定シナリオ: 記事一覧取得APIへの同時アクセス（例: 50〜100同時ユーザー）
- 計測指標: 平均応答時間、エラー率、スループット（req/sec）
- 対象環境: ステージング環境相当のDBサイズ（数千〜数万件の記事データ）で実施することが望ましい（開発DBの少量データでは実運用の負荷傾向を再現できないため）

---

## テストの実行方法（想定）

- フレームワーク: JUnit5 + Spring Boot Test（`spring-boot-starter-test`は導入済み）
- Controller層: `@WebMvcTest` + `MockMvc` + Mockito（Service層をモック化し、DBに依存しない単体テスト）
- 実行コマンド:
```bash
./gradlew test
```
