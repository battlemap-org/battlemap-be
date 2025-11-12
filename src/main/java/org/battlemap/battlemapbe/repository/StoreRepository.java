package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Stores, Long> {
    // 가게 이름으로 조회
    Optional<Stores> findByStoreName(String storeName);

    // 카카오 플레이스 ID로 조회
    Optional<Stores> findByKakaoPlaceId(String kakaoPlaceId);
}
