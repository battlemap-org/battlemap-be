package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.DongLeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.battlemap.battlemapbe.service.LeagueService;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class LeagueController {

    private final DongLeaderboardService dongLeaderboardService;
    private final LeagueService leagueService;

    /**
     * 특정 동 점령 현황
     * 예시: GET /api/regions/부천시/dongs/역곡동/leaderboard
     */
    @GetMapping("/{cityName}/dongs/{dongName}/leaderboard")
    public ResponseEntity<ApiResponse<DongLeaderboardResponse>> getDongLeaderboard(
            Authentication authentication,
            @PathVariable String cityName,
            @PathVariable String dongName
    ) {
        // 인증 확인
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("UNAUTHORIZED", "인증되지 않은 사용자입니다.", 401));
        }

        DongLeaderboardResponse response = dongLeaderboardService.getDongLeaderboard(dongName);
        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }

    // ✅ cityName을 PathVariable로 받는다
    @GetMapping("/{cityName}/leaderboard")
    public LeagueService.LeagueResponse getLeaderboardByCity(@PathVariable String cityName) {
        return leagueService.getMonthlyLeaderboard();
    }
}
