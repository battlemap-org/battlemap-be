package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongPointRowDto;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.PointQueryService;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;
    private final PointQueryService pointQueryService;

    // 사용자 포인트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserPoints(Authentication authentication) {
        String loginId = authentication.getName();
        int currentPoint = userService.getUserPoints(loginId);
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("currentPoint", currentPoint), 200)
        );
    }

    /** 동별 점령 포인트 + 완료 퀘스트 수 조회 */
    @GetMapping("/regions/dongs")
    public ResponseEntity<ApiResponse<List<DongPointRowDto>>> getMyDongPoints(Authentication authentication) {
        String loginId = authentication.getName();
        List<DongPointRowDto> result = pointQueryService.getMyDongPoints(loginId);
        return ResponseEntity.ok(ApiResponse.success(result, 200));
    }
}


