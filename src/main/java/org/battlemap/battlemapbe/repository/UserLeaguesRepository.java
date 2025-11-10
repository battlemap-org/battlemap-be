package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLeaguesRepository extends JpaRepository<UserLeagues, Long> {

    // ✅ 현재 시즌 리더보드 조회
    @Query(value = """
        SELECT u.name AS nickname, SUM(ul.league_point) AS totalPoints
        FROM userleagues ul
        JOIN users u ON ul.user_id = u.user_id
        JOIN leagues l ON ul.league_id = l.league_id
        WHERE l.start_date <= NOW()
          AND l.end_date >= NOW()
        GROUP BY u.name
        ORDER BY totalPoints DESC
    """, nativeQuery = true)
    List<Object[]> findCurrentMonthLeaderboard();


    // ✅ 특정 유저의 리그 포인트 정보 조회 (엔티티 버전)
    @Query("SELECT ul FROM UserLeagues ul WHERE ul.users.userId = :userId")
    Optional<UserLeagues> findByUsers_UserId(@Param("userId") Long userId);


    // ✅ 특정 유저의 리그포인트 0으로 초기화
    @Query(value = "UPDATE userleagues SET league_point = 0 WHERE user_id = :userId", nativeQuery = true)
    void resetLeaguePointsByUser(@Param("userId") Long userId);
}
