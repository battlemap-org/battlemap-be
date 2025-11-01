package org.battlemap.battlemapbe.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final UserRepository userRepository;


     // 보유 포인트 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserPoints(@PathVariable Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND)
                );

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