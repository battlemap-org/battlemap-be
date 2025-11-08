package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.service.LeagueService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class LeagueController {

    private final LeagueService leagueService;

    // ✅ cityName을 PathVariable로 받는다
    @GetMapping("/{cityName}/leaderboard")
    public LeagueService.LeagueResponse getLeaderboardByCity(@PathVariable String cityName) {
        return leagueService.getMonthlyLeaderboard();
    }
}
