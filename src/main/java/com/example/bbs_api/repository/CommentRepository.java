package com.example.bbs_api.repository;

import com.example.bbs_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * コメント情報を操作するJPAリポジトリインターフェース。
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 指定された記事IDに紐づくコメントを、作成日時の昇順で取得する。
     *
     * @param articleId 記事ID
     * @return コメントのリスト
     */
    List<Comment> findByArticleIdOrderByCreatedAtAsc(Long articleId);
}