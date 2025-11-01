package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody Users user) {
        userService.registerUser(user);
        return new ResponseEntity<>(
                ApiResponse.success(Map.of("message", "회원가입 성공"), 201),
                HttpStatus.CREATED
        );
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody Users user) {
        // UserService에서 로그인 처리 + JWT 발급
        String token = userService.login(user.getId(), user.getPw());
        return ResponseEntity.ok(
           ApiResponse.success(Map.of("token", token), 200)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("message", "로그아웃 성공"), 200)
        );
    }
}