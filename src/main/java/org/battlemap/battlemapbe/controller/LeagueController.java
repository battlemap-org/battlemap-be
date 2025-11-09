package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.DongLeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class LeagueController {

    private final DongLeaderboardService dongLeaderboardService;

    /**
     * 특정 동 점령 현황
     * 예시: GET /api/regions/부천시/dongs/역곡동/leaderboard
     *
     * cityName은 지금은 사용하지 않고, dongName 기준으로 조회만 수행.
     * (서비스 시그니처를 건드리지 않기 위해 그대로 둠)
     */
    @GetMapping("/{cityName}/dongs/{dongName}/leaderboard")
    public ResponseEntity<ApiResponse<DongLeaderboardResponse>> getDongLeaderboard(
            @PathVariable String cityName,
            @PathVariable String dongName
    ) {
        DongLeaderboardResponse response = dongLeaderboardService.getDongLeaderboard(dongName);
        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }
}
