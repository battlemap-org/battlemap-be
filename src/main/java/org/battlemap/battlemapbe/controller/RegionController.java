package org.battlemap.battlemapbe.controller;

import org.battlemap.battlemapbe.model.Region;
import org.battlemap.battlemapbe.service.RegionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;
    private final RestTemplate restTemplate = new RestTemplate();

    //  application.properties에서 Kakao API Key 가져오기
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    // 시 이름으로 DB의 모든 동 목록 반환
    @GetMapping("/{cityName}/dongs")
    public ResponseEntity<List<Region>> getDongsByCity(Authentication authentication, @PathVariable String cityName) {
        String userId = authentication.getName();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Region> regions = regionService.getRegionsByCity(cityName);
        return ResponseEntity.ok(regions);
    }

    // 카테고리 목록 조회
    @GetMapping("/{cityName}/dongs/{dongName}/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories(
            Authentication authentication,
            @PathVariable String cityName,
            @PathVariable String dongName) {

        String userId =  authentication.getName();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Map<String, String>> categories = List.of(
                Map.of("name", "식당", "code", "FD6"),
                Map.of("name", "카페", "code", "CE7"),
                Map.of("name", "문화·체험", "code", "CULTURE"),
                Map.of("name", "숙박", "code", "AD5")
        );

        return ResponseEntity.ok(categories);
    }
}
