package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.UserCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/categories")
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<?>> getMostActiveCategory(Authentication authentication) {
        String loginId = authentication.getName();
        String topCategory = userCategoryService.findMostActiveCategory(loginId);
        return ResponseEntity.ok(ApiResponse.success(topCategory, 200));
    }
}
