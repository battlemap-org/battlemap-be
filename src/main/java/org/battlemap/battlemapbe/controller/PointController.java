package org.battlemap.battlemapbe.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserPoints(@PathVariable Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        PointResponse dto = new PointResponse(
                user.getUserId(),
                user.getName(),
                user.getPoint()
        );

        return ResponseEntity.ok(ApiResponse.success(dto, 200));
    }

    @Getter
    @AllArgsConstructor
    static class PointResponse {
        private Long userId;
        private String name;
        private int point;
    }
}
