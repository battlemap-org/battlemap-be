package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.DongLeaderboardService;
import org.battlemap.battlemapbe.service.LeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class LeagueController {

    private final DongLeaderboardService dongLeaderboardService;
    private final LeagueService leagueService;

    // 특정 동 리더보드 조회
    // 예시: GET /api/regions/부천시/dongs/역곡동/leaderboard
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

        String loginId = authentication.getName();

        // 로그인 유저 포함 동별 리더보드 조회
        DongLeaderboardResponse response =
                dongLeaderboardService.getDongLeaderboard(loginId, dongName);

        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }

    // 시(市) 단위 리그 리더보드 조회 (시즌 포인트 포함)
    // 예시: GET /api/regions/부천시/leaderboard
    @GetMapping("/{cityName}/leaderboard")
    public ResponseEntity<ApiResponse<LeagueService.LeagueResponse>> getLeaderboardByCity(
            Authentication authentication,
            @PathVariable String cityName
    ) {
        // 인증 확인
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("UNAUTHORIZED", "인증되지 않은 사용자입니다.", 401));
        }

        String loginId = authentication.getName();

        // loginId와 cityName을 서비스로 전달
        LeagueService.LeagueResponse response = leagueService.getMonthlyLeaderboard(loginId, cityName);

        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }
}
