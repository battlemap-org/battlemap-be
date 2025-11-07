package org.battlemap.battlemapbe.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserCategoryRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * ✅ MySQL용: 유저별 가장 많이 퀘스트 완료한 카테고리 조회
     * - user_quests → quests → stores → categories 조인 기반
     */
    public Optional<Object[]> findTopCategoryByUser(Long userId) {
        Object[] result = (Object[]) em.createNativeQuery("""
            SELECT c.category_name AS category, COUNT(*) AS cnt
            FROM user_quests uq
            JOIN quests q ON uq.quest_id = q.quest_id
            JOIN stores s ON q.store_id = s.store_id
            JOIN categories c ON s.category_id = c.category_id
            WHERE uq.user_id = :userId
              AND uq.is_completed = 1
            GROUP BY c.category_name
            ORDER BY cnt DESC
            LIMIT 1
        """)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(result);
    }
}
