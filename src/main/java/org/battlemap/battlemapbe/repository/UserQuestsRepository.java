package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuestsRepository extends JpaRepository<UserQuests, Long> {
    Optional<UserQuests> findByUsersAndQuests(Users users, Quests quests);

    // 사용자 기반 퀘스트 수 조회 (마이페이지)
    long countByUsers(Users user);

    // 오늘의 퀘스트 조회 (인증용)
    Optional<UserQuests> findByUsersAndTodayQuests(Users users, TodayQuests todayQuests);
}
