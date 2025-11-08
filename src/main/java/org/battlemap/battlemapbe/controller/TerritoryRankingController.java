package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.TerritoryTeamRankDto;
import org.battlemap.battlemapbe.service.TerritoryRankingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/territories")
public class TerritoryRankingController {

    private final TerritoryRankingService territoryRankingService;

    // 지역별(테리토리) 팀 랭킹 조회: /api/territories/{territoryId}/rank?top=10
    @GetMapping("/{territoryId}/rank")
    public List<TerritoryTeamRankDto> getTerritoryRank(
            @PathVariable Long territoryId,
            @RequestParam(name = "top", defaultValue = "10") int topN
    ) {
        return territoryRankingService.getTerritoryTeamRank(territoryId, topN);
    }
}
