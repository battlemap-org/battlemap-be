package org.battlemap.battlemapbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;

import java.util.List;

public interface UserLeaguesRepository extends JpaRepository<UserLeagues, Long> {

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
}
