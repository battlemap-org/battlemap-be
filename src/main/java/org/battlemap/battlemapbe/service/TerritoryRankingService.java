package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.TerritoryTeamRankDto;
import org.battlemap.battlemapbe.repository.TerritoryRankingQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class TerritoryRankingService {
    private final TerritoryRankingQueryRepository rankingQueryRepository;
    @Value("${app.mock-ranking:false}") private boolean mock;

    public List<TerritoryTeamRankDto> getTerritoryTeamRank(Long territoryId, int topN) {
        if (topN <= 0) topN = 10;
        if (mock) {
            return List.of(
                    new TerritoryTeamRankDto(1, 1L, "팀A", 70),
                    new TerritoryTeamRankDto(2, 2L, "팀B", 50),
                    new TerritoryTeamRankDto(3, 3L, "팀C", 30)
            );
        }
        return rankingQueryRepository.findTeamRankingByTerritory(territoryId, topN);
    }
}
