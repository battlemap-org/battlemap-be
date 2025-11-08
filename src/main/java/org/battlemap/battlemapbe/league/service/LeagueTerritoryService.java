package org.battlemap.battlemapbe.league.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.league.dto.TerritoryDto;
import org.battlemap.battlemapbe.model.Region;
import org.battlemap.battlemapbe.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueTerritoryService {

    private final RegionService regionService;

    /**
     * 특정 리그(leagueId)에 속한 동 리스트 반환.
     * 현재는 테스트용으로 leagueId와 상관없이 "부천시"의 모든 동을 반환.
     * 추후 리그별 필터링 로직 연결 예정.
     */
    public List<TerritoryDto> getTerritories(Long leagueId) {
        // DB에서 "부천시"에 해당하는 모든 Region 데이터 조회
        List<Region> regions = regionService.getRegionsByCity("부천시");

        // Region → TerritoryDto 변환
        return regions.stream()
                .map(region -> TerritoryDto.builder()
                        .polygonId(region.getId())  // 리그 지도상 구역 ID
                        .name(region.getName())     // "부천시 역곡동"
                        .latitude(region.getLatitude())
                        .longitude(region.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }
}
