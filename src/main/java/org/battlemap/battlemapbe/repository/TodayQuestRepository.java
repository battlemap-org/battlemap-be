package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodayQuestRepository extends JpaRepository<TodayQuests, Long> {
    Optional<TodayQuests> findFirstByCreatedAtBetween(java.time.LocalDateTime startOfDay, java.time.LocalDateTime endOfDay);
}
