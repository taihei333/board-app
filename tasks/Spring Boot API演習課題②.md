# 課題：API の実装

## 概要

掲示板アプリの API を Spring Boot で実装してください。

## 実装する API

Spring Boot API 演習課題 ① で定義した、以下の 7 本の API を実装してください：

1. ログインユーザー情報取得 API
2. 記事一覧取得 API
3. 記事登録 API
4. 記事詳細取得 API
5. 記事更新 API
6. 記事削除 API
7. コメント登録 API

※ ログイン API は実装済みです。

## 実装要件

データベースアクセスには [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/) を使用してください

## スケジュール

- API 実装：計 5 日
  - ログインユーザー情報取得 API：0.5 日
  - 記事一覧取得 API：1 日
  - 記事登録 API：1 日
  - 記事詳細取得 API：0.5 日
  - 記事更新 API：0.5 日
  - 記事削除 API：0.5 日
  - コメント登録 API：1 日

## タスク管理

いろんなタスク管理ツールで出来ることを今回は、GitHub Projects を使ってタスク管理を行います。

### プロジェクト作成方法

[Spring Boot API課題テンプレート](https://github.com/orgs/KIR-SHARE-ISSUES/projects/77) から自身のプロジェクトを作成します。

「Use this template」ボタンをクリック

<img width="800" alt="image" src="https://github.com/user-attachments/assets/e6ae0b4d-1732-43de-be10-e61077eb1cd4" />

Owner：KIR-SHARE-ISSUES を選択<br>
New project name：※Spring Boot API課題のリポジトリ名<br>
「Use template」でプロジェクト作成

<img width="800" alt="image" src="https://github.com/user-attachments/assets/bf18adf1-3ac1-410f-a182-74ba3a5c79f4" />

### Issueの有効化

「▼」をクリックして「Convert to issue」を選択

<img width="800" alt="image" src="https://github.com/user-attachments/assets/e4db82f1-0ed8-4797-bde3-7c1ed5bee9e3" />

作成した自身のSpring Boot API課題のリポジトリを選択

<img width="800" alt="image" src="https://github.com/user-attachments/assets/f04e570d-47b9-46da-a03c-8ac44c7c8ff7" />

Issueが有効化され選択したリポジトリに登録されます

<img width="800" alt="image" src="https://github.com/user-attachments/assets/0236f730-1949-444b-a6b6-eee352e80f9e" />

「Assigneers」を自身に設定し、上述したスケジュールに沿って「Start data」「End date」を設定してください **※順不同**

<img width="800" alt="image" src="https://github.com/user-attachments/assets/c35f8779-4785-4454-8da4-2810b451db4f" />

Spring Boot API課題のリポジトリに戻り、「Issue」に「7」と表示されていれば設定完了です

<img width="800" alt="image" src="https://github.com/user-attachments/assets/7350bab7-af04-48fb-87e8-cee1d448b34a" />


※以下の部分を変えると色々なUIに変更できます。
<img width="510" height="574" alt="スクリーンショット 2025-10-29 18 38 20" src="https://github.com/user-attachments/assets/578ae944-5298-4e41-a1f4-3b3f5026f68f" />

例）`Board`に変更すると、チケット管理のような見た目になります。使いやすいものを使用してください。
