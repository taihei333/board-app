package com.example.bbs_api.repository;

import com.example.bbs_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザー情報を操作するJPAリポジトリインターフェース。
 * <p>
 * Userエンティティに対する基本的なCRUD操作のほか、
 * メールアドレスによるユーザー検索や存在確認のメソッドを提供する。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 指定されたメールアドレスに該当するユーザーを検索する。
     *
     * @param email 検索対象のメールアドレス
     * @return ユーザーが存在する場合はOptionalにUserを含む。存在しない場合は空のOptional。
     */
    Optional<User> findByEmail(String email);

    /**
     * 指定されたメールアドレスのユーザーが存在するかどうかを判定する。
     *
     * @param email 判定対象のメールアドレス
     * @return 存在すればtrue、存在しなければfalse
     */
    boolean existsByEmail(String email);
}
