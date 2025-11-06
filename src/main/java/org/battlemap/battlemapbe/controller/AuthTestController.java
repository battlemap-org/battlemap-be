package org.battlemap.battlemapbe.controller;

import org.battlemap.battlemapbe.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test") // 기존 베이스 경로 유지
public class AuthTestController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthTestController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /** 토큰이 유효한지 확인 (Authorization: Bearer <token>) */
    // 인증된 사용자 정보 확인용
    @GetMapping("/auth-check")
    public ResponseEntity<?> checkAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 실패 또는 토큰 없음"));
        }
        String userId = authentication.getName(); // JwtAuthenticationFilter에서 넣어준 subject
        return ResponseEntity.ok(Map.of("message", "인증 성공", "userId", userId));
    }

    /** 테스트용 모의 로그인: userId로 JWT 발급 (실서비스에서는 제거/비활성 권장) */
    @PostMapping("/mock-login")
    public ResponseEntity<?> mockLogin(@RequestParam String userId) {
        String token = jwtTokenProvider.generateToken(userId);
        return ResponseEntity.ok(Map.of("token", token, "userId", userId));
    }
}
