package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.region.UserDongPointResponse;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.PointService;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointController {

    private final UserService userService;
    private final PointService pointService;

    // ✅ 기존: 사용자 전체 포인트 조회 (/api/points)
    @GetMapping("/points")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserPoints(Authentication authentication) {
        String loginId = authentication.getName();
        int currentPoint = userService.getUserPoints(loginId);
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("currentPoint", currentPoint), 200)
        );
    }

    // ✅ 신규: 나의 동별 포인트 & 완료 퀘스트 수 조회 (/api/users/regions/dongs/points)
    @GetMapping("/users/regions/dongs/points")
    public ResponseEntity<ApiResponse<List<UserDongPointResponse>>> getMyDongPointsByDong(
            @RequestParam String cityName,
            Authentication authentication
    ) {
        String loginId = authentication.getName();

        List<UserDongPointResponse> result =
                pointService.getMyDongPoints(loginId, cityName);

        return ResponseEntity.ok(
                ApiResponse.success(result, 200)
        );
    }
}
