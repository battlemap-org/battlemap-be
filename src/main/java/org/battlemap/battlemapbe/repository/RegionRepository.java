package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    // '부천시' 등 특정 시/군명이 포함된 지역들 전체 조회
    List<Region> findByNameContaining(String cityName);
    Optional<Region> findByName(String name);
}
