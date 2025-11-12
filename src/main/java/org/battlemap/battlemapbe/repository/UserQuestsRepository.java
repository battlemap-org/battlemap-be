package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.Categories;
import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserQuestsRepository extends JpaRepository<UserQuests, Long> {
    Optional<UserQuests> findByUsersAndQuests(Users users, Quests quests);

    // 사용자 기반 퀘스트 수 조회 (마이페이지)
    long countByUsers(Users user);

    // 오늘의 퀘스트 조회 (인증용)
    Optional<UserQuests> findByUsersAndTodayQuests(Users users, TodayQuests todayQuests);

    @Query("SELECT COUNT(uq) > 0 FROM UserQuests uq " +
            "JOIN uq.quests q " + // uq.quests가 NOT NULL인 레코드만 (가게 퀘스트)
            "JOIN q.stores s " +
            "WHERE uq.users = :user " +
            "AND uq.isCompleted = true " +
            "AND uq.completedAt BETWEEN :startOfDay AND :endOfDay " +
            "AND s.dongs = :dong " +
            "AND s.categories = :category")
    boolean hasCompletedStoreQuestMatchingCriteria(
            @Param("user") Users user,
            @Param("dong") Dongs dong,
            @Param("category") Categories category,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
