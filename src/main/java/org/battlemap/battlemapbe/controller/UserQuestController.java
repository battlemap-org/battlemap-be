package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.UserQuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/quests")
@RequiredArgsConstructor
public class UserQuestController {

    private final UserQuestService userQuestService;

    // 토큰 기반 총 퀘스트 수 조회
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalQuestCount(Authentication authentication) {
        String loginId = authentication.getName(); // 토큰에서 loginId 추출
        Map<String, Object> result = userQuestService.getQuestCountByLoginId(loginId);
        return ResponseEntity.ok(ApiResponse.success(result, 200));
    }
    // 완료된 퀘스트 수 조회
    @GetMapping("/complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompletedQuestCount(Authentication authentication) {
        String loginId = authentication.getName();
        Map<String, Object> result = userQuestService.getCompletedQuestCountByLoginId(loginId);
        return ResponseEntity.ok(ApiResponse.success(result, 200));
    }
}
