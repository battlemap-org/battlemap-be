package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication,
            @PathVariable String cityName,
            @PathVariable String dongName,
            @RequestParam String category) {

        String loginId = authentication.getName();

        Map<String, Object> stores = storeService.getStoresByRegionAndCategory(loginId, cityName, dongName, category);
        return ResponseEntity.ok(stores);
    }
}
