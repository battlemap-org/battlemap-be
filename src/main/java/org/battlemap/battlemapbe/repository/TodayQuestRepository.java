package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayQuestRepository extends JpaRepository<TodayQuests, Long> {
}