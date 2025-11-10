package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;

    /**
     * 🔹 사용자 보유 포인트 조회 (로그인 아이디 기반)
     * 예시: GET /api/points
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserPoints(Authentication authentication) {
        String loginId = authentication.getName(); // JWT에서 loginId 추출
        int currentPoint = userService.getUserPoints(loginId);
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("currentPoint", currentPoint), 200)
        );
    }
}
