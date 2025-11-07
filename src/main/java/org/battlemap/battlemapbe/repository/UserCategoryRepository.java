package org.battlemap.battlemapbe.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserCategoryRepository {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Object[]> findTopCategoryByUser(Long userId) {
        return (List<Object[]>) em.createNativeQuery("""
            SELECT category_group_name AS category, COUNT(*) AS cnt
            FROM user_activities
            WHERE user_id = :userId
              AND action_type = 'QUEST_COMPLETE'
            GROUP BY category_group_name
            ORDER BY cnt DESC
            LIMIT 1
        """)
                .setParameter("userId", userId)
                .getResultList();
    }
}
