package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.service.LeagueQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/league")
public class LeagueRestController {

    private final LeagueQueryService queryService;

    @GetMapping("/{areaId}/leaderboard")
    public List<LeaderboardResponseDto> leaderboard(
            @PathVariable String areaId,
            @RequestParam(defaultValue = "2025S3") String seasonId
    ) {
        return queryService.getSnapshot(areaId, seasonId);
    }
}
