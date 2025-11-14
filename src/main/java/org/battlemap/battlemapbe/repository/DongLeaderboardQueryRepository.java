package org.battlemap.battlemapbe.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DongLeaderboardQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public List<DongLeaderboardResponse.Player> findDongLeaderboardByDongNameAndPeriod(
            String dongName,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String jpql =
                "SELECT new org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse$Player(" +
                        "       uq.users.name, " +
                        "       SUM(q.rewardPoint), " +
                        "       NULL" + // userColorCode 필드에 대한 임시 값
                        ") " +
                        "FROM UserQuests uq " +
                        "JOIN uq.quests q " +
                        "JOIN q.stores s " +
                        "WHERE uq.isCompleted = true " +
                        "  AND s.dongs.dongName = :dongName " +
                        "  AND uq.completedAt BETWEEN :start AND :end " +
                        "GROUP BY uq.users.name " +
                        "ORDER BY SUM(q.rewardPoint) DESC";

        return em.createQuery(jpql, DongLeaderboardResponse.Player.class)
                .setParameter("dongName", dongName)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}