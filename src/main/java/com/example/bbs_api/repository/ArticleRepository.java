package com.example.bbs_api.repository;

import com.example.bbs_api.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 記事情報を操作するJPAリポジトリインターフェース。
 * <p>
 * 基本的なCRUD操作に加え、動的な検索条件を組み立てるために
 * JpaSpecificationExecutorを利用する。
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
}
