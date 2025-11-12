package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.dto.login.LoginResponse;
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

    // 로그인 (LoginResponse DTO 반환)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody Users user) {
        LoginResponse response = userService.login(user.getId(), user.getPw());
        return ResponseEntity.ok(
                ApiResponse.success(response, 200)
        );
    }

    // 로그아웃 api
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("message", "로그아웃 성공"), 200)
        );
    }
    // 사용자 이름 조회
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserName(
            @RequestHeader("Authorization") String token) {

        Map<String, Object> result = userService.getUserNameByToken(token);
        return ResponseEntity.ok(
                ApiResponse.success(result, 200)
        );
    }
}
