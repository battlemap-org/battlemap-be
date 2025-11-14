package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Leagues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeaguesRepository extends JpaRepository<Leagues, Long> {

    // 현재 진행 중인 리그 (조회용)
    @Query("SELECT l FROM Leagues l WHERE l.startDate <= :now AND l.endDate >= :now")
    Optional<Leagues> findCurrentLeague(@Param("now") LocalDateTime now);

    // 시즌 종료 (endDate 지남) & 아직 settled = false 인 리그들 (자동 정산 대상)
    @Query("SELECT l FROM Leagues l WHERE l.endDate <= :now AND l.settled = false")
    List<Leagues> findExpiredUnsettledLeagues(@Param("now") LocalDateTime now);
}
