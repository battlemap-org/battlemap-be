package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Stores; // Stores import 추가
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestsRepository extends JpaRepository<Quests, Long> {
    // 가게별 quest 조회
    List<Quests> findByStores_StoreId(Long storeId);

    // 기존 퀘스트 존재 확인
    boolean existsByStores(Stores stores);
}
