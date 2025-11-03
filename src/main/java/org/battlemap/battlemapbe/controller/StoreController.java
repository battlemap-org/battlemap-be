package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // ex) /api/regions/부천시/dongs/역곡동/stores?category=CE7
    // 가게 조회 api
    @GetMapping("/{cityName}/dongs/{dongName}/stores")
    public ResponseEntity<Map<String, Object>> getStoresByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String cityName,
            @PathVariable String dongName,
            @RequestParam String category) {

        // 인증 확인
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "인증되지 않은 사용자입니다."));
        }

        // 로그인한 사용자 정보 (필요하다면 userDetails.getUsername()으로 loginId 조회 가능)
        String loginId = userDetails.getUsername();

        Map<String, Object> stores = storeService.getStoresByRegionAndCategory(cityName, dongName, category);
        return ResponseEntity.ok(stores);
    }
}
