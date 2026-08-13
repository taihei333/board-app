# Spring Boot モダン掲示板アプリ（API）

教育側で用意した Spring Boot プロジェクトを使って課題を行います。

## リポジトリの準備

### リポジトリの作成

テンプレートリポジトリ `spring-boot-api-template` からリポジトリを作成

「Use this template」ボタンをクリック

<img width="676" height="98" alt="image" src="https://github.com/user-attachments/assets/50fbee88-c21f-46a4-b2ec-43422d082eac" />

「Repository name」を入力して「Create repository」ボタンをクリックして作成

リポジトリ名は [リポジトリ命名規則](https://github.com/KIR-SHARE-ISSUES/template?tab=readme-ov-file#-%E3%83%AA%E3%83%9D%E3%82%B8%E3%83%88%E3%83%AA%E5%91%BD%E5%90%8D%E8%A6%8F%E5%89%87) を参照

<img width="667" height="361" alt="image" src="https://github.com/user-attachments/assets/153f3a15-91e4-49c2-88e6-9bd5c9ceb023" />

### リポジトリのクローン

Mac はターミナル、Windows は WSL ターミナルを開いて、ホームディレクトリへ移動

```bash
cd ~
```

クローン

```bash
git clone [url]
```

## 開発環境の準備

### IntelliJ

IDE（統合開発環境）は IntelliJ を使います。以下から **IntelliJ IDEA Community 版（無料版）** をダウンロードしてください。

https://www.jetbrains.com/ja-jp/idea/download/

### IntelliJ の設定（ビルドとコンパイラ）

クローンしたプロジェクトを IntelliJ で開きます。

このプロジェクトでは **Java 21** を使用します。

そのため、ビルドとコンパイラで使用する Java のバージョンを 21 に設定してください。

## ステップ 1：Java 21 SDK を IntelliJ に追加（まだの場合）

1. IntelliJ IDEA を開く
2. メニューから **「ファイル」→「プロジェクト構造」**
   （またはショートカット `Ctrl + Alt + Shift + S`）
3. 左のメニューから **「プラットフォーム設定」→「SDK」** を選択
4. 上部の「＋」ボタン → **「JDK」** を選択
5. Java 21 のインストール先を選ぶ

- 例: `C:\Program Files\Java\jdk-21` や `/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home` など

6. 名前を「21」などに変更し、`OK` をクリック

## ステップ 2：プロジェクトの SDK を Java 21 に設定

1. 同じく「プロジェクト構造」画面で、左メニューから **「プロジェクト」** を選択
2. 「プロジェクト SDK」 を `21` に設定
3. 「プロジェクトの言語レベル」も `21（switchのパターンマッチングなど）` を選択
4. `適用 → OK`

## ステップ 3：モジュール設定を確認

1. 「プロジェクト構造」画面で左の **「モジュール」** を選択
2. 「ソース」タブで **「言語レベル」** を `21` に変更
3. 「依存関係」タブで **「モジュール SDK」** を `21` に設定
4. `適用 → OK`

## ステップ 4：コンパイラのバージョン設定

1. メニューから `ファイル → 設定`（Mac の場合は `IntelliJ IDEA → 設定`）を開く
2. 左メニューから **「ビルド、実行、デプロイ」→「コンパイラ」→「Java コンパイラ」** を選択
3. 「モジュールごとのバイトコードのバージョン」の欄で、該当モジュールを `21` に設定

### データベース

Spring Boot では、Docker Compose Support を利用することで、  
アプリケーションの起動時に必要な Docker コンテナ（例：PostgreSQL など）を自動で立ち上げることができます。
今回は、Docker Compose Support を利用しているプロジェクトなので、
従来のように `docker compose up` コマンドを手動で実行する必要はありません。  
**Docker Desktop が起動していれば、自動的にコンテナが立ち上がります。**

データベースの構成は `/docs/database/erd.md` に ER 図を記載しています。<br>
https://github.com/KIR-SHARE-ISSUES/spring-boot-api-template/blob/main/docs/database/erd.md

データベースコンテナが立ち上がると自動的にテーブルを作成し、ダミーデータを登録する設定をしているので、すぐに課題に取り掛かることができます。

Spring Boot を停止すると、コンテナも停止&削除されます。

### DB クライアントツール（DBeaver）

以下からデータベースツール DBeaver をインストールしてください。

[データベースツール | LITTLE HEROES](https://respawn.littleheroes.jp/w/courses/1977)

### API の動作確認ツール(API クライアントツール)

API の動作確認には、以下のようなツールを使ってテストしてください。

---

### Swagger UI

API 仕様書を見ながら、**ブラウザ上ですぐにリクエストを送れるツール**です。  
自分でコードを書かなくても、ボタン 1 つで API を試せます。

- **使い方**：今回の課題プロジェクトのように Swagger UI コンテナが組み込まれている場合、`http://localhost:8002` にアクセスするだけで OK です。
- **参考リンク**：[Swagger UI 公式](https://swagger.io/tools/swagger-ui/)

---

### Postman

複雑な API リクエストの作成・保存ができる**有名な API クライアント**です。  
認証やパラメータの設定も簡単に行えます。アプリ版と Web 版があります。

- **Web 版**：[Postman for Web](https://web.postman.co/)
- **デスクトップ版のダウンロード**：[Postman Download](https://www.postman.com/downloads/)

---

### APIDog

Postman に似ていますが、**より軽くて速いのが特徴**の Web ベースのツールです。  
インストール不要でサクッと API を試したいときにおすすめです。

- **Web 版**：[APIDog](https://apidog.com/)

---

### プロジェクト環境

- 使用言語：Java 21
- フレームワーク：Spring Boot 3.5.4
- ビルドツール：Gradle
- データベース：PostgreSQL
- API 設計書：Swagger UI

⚠️ 注意：ビルドツールについて

このプロジェクトは**Gradle**を使用しています。
そのため、今までの課題のMavenプロジェクトで一般的に存在する`pom.xml`はありません。

代わりに以下のファイルが存在します：

- `build.gradle`→ 依存関係やビルド設定を記述するファイル
- `settings.gradle` → プロジェクト名やモジュール定義を記述するファイル

💡 「pom.xml が見つからない！」と思った場合は、Gradleを使っていることを思い出してください。

## 学習コンテンツ

### 1. [API インターフェース設計書の作成](./tasks/Spring%20Boot%20API演習課題①.md)

**目的**: API 設計を体験し、面談で話せるようにイメージできるようにすること

**学習内容**:

- REST API 設計原則（リソース設計、HTTP メソッド、ステータスコードなど）
- OpenAPI（API 設計用の仕様書フォーマット）についての理解
- Swagger Editor を使った API 設計書（OpenAPI 形式）の作成方法

### 2. [API の実装](./tasks/Spring%20Boot%20API演習課題②.md)

**目的**: Spring Boot を使用した API の実装

**学習内容**:

- Spring Boot による REST API 実装
- Spring Data JPA によるデータベースアクセス
- GitHub Issues を用いたタスク管理と進捗管理

**参考リポジトリ**:
過去の先輩たちのリポジトリです。参考にして自分の掲示板に実装してください。
https://github.com/KIR-SHARE-ISSUES/2511-tomatsu-r-springboot-api-1
