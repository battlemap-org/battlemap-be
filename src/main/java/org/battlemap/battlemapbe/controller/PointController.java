package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;

    // 사용자 포인트 조회 api
    @GetMapping("/points")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserPoints(@AuthenticationPrincipal String loginId) {
        int currentPoint = userService.getUserPoints(loginId);
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("currentPoint", currentPoint), 200)
        );
    }
}
