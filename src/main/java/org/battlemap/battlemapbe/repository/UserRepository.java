package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    // JPQL 기반 커스텀 쿼리 사용 (id 컬럼 명시)
    @org.springframework.data.jpa.repository.Query("SELECT u FROM Users u WHERE u.id = :id")
    Optional<Users> findByLoginId(@org.springframework.data.repository.query.Param("id") String id);

    Optional<Users> findByEmail(String email);
}