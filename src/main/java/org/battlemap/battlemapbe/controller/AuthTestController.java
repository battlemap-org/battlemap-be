package org.battlemap.battlemapbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class AuthTestController {

    // 🔹 인증된 사용자 정보 확인용
    @GetMapping("/auth-check")
    public ResponseEntity<?> checkAuth(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("{\"error\": \"인증 실패 또는 토큰 없음\"}");
        }
        String userId = authentication.getName();
        return ResponseEntity.ok("{\"message\": \"인증 성공\", \"userId\": \"" + userId + "\"}");
    }
}