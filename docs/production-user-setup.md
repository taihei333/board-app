# 本番環境へのログインユーザー事前登録手順（SQL直接挿入）

サインアップAPI（`POST /api/v1/signup`）を使わず、本番のMariaDBに直接ユーザーを1件挿入する手順。
アプリ起動前にユーザーを存在させておきたい場合に使用する。

## 前提

- `users`テーブルの構造（`User.java`より）

  | カラム | 型 | 制約 |
  |---|---|---|
  | id | BIGINT | 自動採番（IDENTITY） |
  | name | VARCHAR | NOT NULL、最大100文字 |
  | email | VARCHAR | NOT NULL、UNIQUE |
  | password | VARCHAR | NOT NULL、BCryptハッシュを格納 |
  | role | VARCHAR | NOT NULL（手動INSERT時は明示的に`USER`を指定する。JPA経由のデフォルト値`USER`はSQL直接実行には適用されないため） |

- パスワードは平文で保存してはいけない。Spring Security側は`BCryptPasswordEncoder`（strength 10）でハッシュ化したものと突き合わせて認証するため、**事前にBCryptハッシュへ変換した文字列**を用意する必要がある。

## 手順

### 1. BCryptハッシュを生成する（ローカルのMacで実行）

Pythonの`bcrypt`ライブラリを使う。未インストールの場合は先にインストールする。

```bash
pip3 install bcrypt
```

パスワードをハッシュ化する。`あなたのパスワード`の部分を実際に使いたいパスワードに置き換える。

```bash
python3 -c "import bcrypt; print(bcrypt.hashpw(b'あなたのパスワード', bcrypt.gensalt()).decode())"
```

出力例（`$2a$` または `$2b$` から始まる文字列）：

```
$2b$10$abcdefghijklmnopqrstuvKQOxYzL1a2b3c4d5e6f7g8h9i0j1k2
```

この文字列を控えておく。

### 2. MariaDBに接続する（EC2サーバー上、またはEC2からMariaDBインスタンスへ）

```bash
mysql -h <MariaDBのプライベートIP> -u <DBユーザー名> -p <データベース名>
```

パスワードを求められたら、`DB_PASSWORD`に設定している値を入力する。

### 3. INSERT文を実行する

手順1で生成したBCryptハッシュを`password`カラムにそのまま貼り付ける。

```sql
INSERT INTO users (name, email, password, role)
VALUES ('admin', 'admin@example.com', '$2b$10$abcdefghijklmnopqrstuvKQOxYzL1a2b3c4d5e6f7g8h9i0j1k2', 'USER');
```

- `name`：10文字以内（アプリのバリデーションに準拠させる場合。DB制約自体は100文字までだが、アプリの他機能と挙動を合わせるなら10文字以内にしておく）
- `email`：ログインIDとして使われる。重複不可
- `role`：管理者権限を持たせたい場合は、コード側で`role`をどう判定しているか（`"ADMIN"`などの文字列を期待していないか）を事前に確認してから値を決める

### 4. 挿入結果を確認する

```sql
SELECT id, name, email, role FROM users WHERE email = 'admin@example.com';
```

1件表示されれば成功。`password`カラムの中身が意図したBCryptハッシュになっているかも合わせて確認する（`SELECT password FROM users WHERE email = 'admin@example.com';`）。

### 5. ログインできるか確認する

アプリ起動後、実際にログインAPIでトークンが取得できるか確認する。

```bash
curl -X POST https://api.bbs.taiheinosite.xyz/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"あなたのパスワード"}'
```

ログインに使うパスワードは、手順1でハッシュ化する前の**平文のパスワード**を入力する（ハッシュ文字列ではない）。

## 注意点

- 同じメールアドレスで既にユーザーが存在する場合、`email`のUNIQUE制約によりINSERTがエラーになる。事前に`SELECT`で重複がないか確認しておくとよい。
- BCryptハッシュはソルトを含むため、同じパスワードでも生成するたびに異なる文字列になる。これは正常な挙動であり、認証時はハッシュ同士を文字列比較するのではなく、BCryptのアルゴリズムで検証される。
- パスワードをターミナルの履歴やログに平文で残さないよう、作業後は`history`の該当行削除やターミナルのクリアを検討する。
