package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.DongLeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.battlemap.battlemapbe.service.LeagueService;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class LeagueController {

    private final DongLeaderboardService dongLeaderboardService;

    /**
     * 특정 동 점령 현황
     * 예시: GET /api/regions/dongs/역곡동/leaderboard
     */
    @GetMapping("/dongs/{dongName}/leaderboard")
    public ResponseEntity<ApiResponse<DongLeaderboardResponse>> getDongLeaderboard(
            @PathVariable String dongName
    ) {
        DongLeaderboardResponse response =
                dongLeaderboardService.getDongLeaderboard(dongName);

        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }
    private final LeagueService leagueService;

    // ✅ cityName을 PathVariable로 받는다
    @GetMapping("/{cityName}/leaderboard")
    public LeagueService.LeagueResponse getLeaderboardByCity(@PathVariable String cityName) {
        return leagueService.getMonthlyLeaderboard();
    }
}
