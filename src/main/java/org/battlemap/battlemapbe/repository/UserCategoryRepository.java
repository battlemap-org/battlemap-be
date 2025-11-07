package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserQuests, Long> {

    @Query("""
        SELECT c.categoryName
        FROM UserQuests uq
        JOIN uq.quests q
        JOIN q.stores s
        JOIN s.categories c
        WHERE uq.users.id = :userId
          AND uq.isCompleted = true
        GROUP BY c.categoryName
        ORDER BY COUNT(uq.userQuestId) DESC
    """)
    List<String> findTopCategoriesByUserId(@Param("userId") Long userId); // ✅ 여기 Long이어야 함
}
