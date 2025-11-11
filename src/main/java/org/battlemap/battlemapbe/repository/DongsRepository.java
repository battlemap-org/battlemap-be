package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Dongs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DongsRepository extends JpaRepository<Dongs, Long> {

    // 동 이름으로 단건 조회 (예: "역곡동")
    Optional<Dongs> findByDongName(String dongName);

    // 시 이름으로 동 목록 조회 (Cities 엔티티의 cityName 기준)
    List<Dongs> findByCities_CityName(String cityName);

    // 시 + 동 조합으로 조회
    Optional<Dongs> findByCities_CityNameAndDongName(String cityName, String dongName);
}
