package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.security.JwtTokenProvider;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserPoints(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException("UNAUTHORIZED", "토큰이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            String token = authHeader.substring(7).trim();
            String userId = jwtTokenProvider.validateAndGetUserId(token);

            int points = userService.getUserPoints(userId);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            Map.of(
                                    "userId", userId,
                                    "points", points
                            ),
                            HttpStatus.OK.value()
                    )
            );

        } catch (RuntimeException e) {
            throw new CustomException("INVALID_TOKEN", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            throw new CustomException("SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
