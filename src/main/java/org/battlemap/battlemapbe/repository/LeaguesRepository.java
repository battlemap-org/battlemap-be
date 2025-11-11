package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Leagues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LeaguesRepository extends JpaRepository<Leagues, Long> {

    // 🔹 현재 진행 중인 리그(시즌) 조회 (startDate ≤ now ≤ endDate)
    @Query("SELECT l FROM Leagues l " +
            "WHERE l.startDate <= :now AND l.endDate >= :now")
    Optional<Leagues> findCurrentLeague(@Param("now") LocalDateTime now);
}
