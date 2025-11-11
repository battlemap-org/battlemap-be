package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // 로그인 ID 기준으로 조회
    @Query("SELECT u FROM Users u WHERE u.id = :id")
    Optional<Users> findByLoginId(@Param("id") String id);

    // 이메일 기준 조회
    Optional<Users> findByEmail(String email);

    // 닉네임(name) 기준 조회 — 리그 보너스 지급용
    Optional<Users> findByName(String name);
}
