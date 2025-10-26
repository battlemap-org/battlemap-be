package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Quests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestsRepository extends JpaRepository<Quests, Long> {
    // 가게별 quest 조회
    List<Quests> findByStores_StoreId(Long storeId);
}
