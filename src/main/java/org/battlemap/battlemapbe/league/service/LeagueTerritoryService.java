// src/main/java/org/battlemap/battlemapbe/league/service/LeagueTerritoryService.java
package org.battlemap.battlemapbe.league.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.league.dto.TerritoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeagueTerritoryService {

    public List<TerritoryDto> getTerritories(Long leagueId) {
        return List.of(
                TerritoryDto.builder().name("역곡1구역").code("YG-01").polygonId(101L).build(),
                TerritoryDto.builder().name("역곡2구역").code("YG-02").polygonId(102L).build(),
                TerritoryDto.builder().name("역곡3구역").code("YG-03").polygonId(103L).build()
        );
    }
}
