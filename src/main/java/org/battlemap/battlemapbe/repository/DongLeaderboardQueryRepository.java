package org.battlemap.battlemapbe.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DongLeaderboardQueryRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 특정 동(dongName)에서 완료된 퀘스트 기준
     * 유저별 누적 rewardPoint 합계를 구해 점수 순으로 정렬
     */
    public List<DongLeaderboardResponse.Player> findDongLeaderboardByDongName(String dongName) {

        String jpql =
                "SELECT new org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse$Player(" +
                        "       uq.users.name, " +              // Users.name
                        "       SUM(q.rewardPoint)" +
                        ") " +
                        "FROM UserQuests uq " +
                        "JOIN uq.quests q " +
                        "JOIN q.stores s " +
                        "WHERE uq.isCompleted = true " +
                        "  AND s.dongs.dongName = :dongName " +
                        "GROUP BY uq.users.name " +
                        "ORDER BY SUM(q.rewardPoint) DESC";

        return em.createQuery(jpql, DongLeaderboardResponse.Player.class)
                .setParameter("dongName", dongName)
                .getResultList();
    }
}
