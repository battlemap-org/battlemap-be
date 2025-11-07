package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.service.UserCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/categories")
@RequiredArgsConstructor
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    /**
     * ✅ JWT 인증 기반 유저별 최다 카테고리 조회
     */
    @GetMapping("/top")
    public ResponseEntity<?> getTopCategory(Authentication authentication) {
        String loginId = authentication.getName();
        String topCategory = userCategoryService.findMostActiveCategory(loginId);
        return ResponseEntity.ok(Map.of("topCategory", topCategory));
    }
}
