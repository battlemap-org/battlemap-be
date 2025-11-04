package org.battlemap.battlemapbe.controller;

import org.battlemap.battlemapbe.model.Region;
import org.battlemap.battlemapbe.service.RegionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    // 전체 시/군 목록 조회 (현재는 부천시만)
    @GetMapping("/cities")
    public List<String> getAllCities() {
        return List.of("부천시");
    }

    // 시 선택 저장 (테스트용)
    @PostMapping("/cities/selection")
    public String saveCitySelection(@RequestParam String cityName) {
        return "선택된 시: " + cityName;
    }

    // 사용자별 선택된 시 조회 (예시)
    @GetMapping("/cities/selection/{userId}")
    public String getSelectedCity(@PathVariable Long userId) {
        return "userId " + userId + "의 선택된 시: 부천시";
    }

    // 시 이름으로 DB의 모든 동 목록 반환
    @GetMapping("/{cityName}/dongs")
    public List<Region> getDongsByCity(@PathVariable String cityName) {
        return regionService.getRegionsByCity(cityName);
    }

    // 카테고리 예시 조회
    @GetMapping("/{cityName}/dongs/{dongName}/categories")
    public List<String> getCategories() {
        return List.of("FD6 (음식점)", "CE7 (카페)", "CS2 (편의점)");
    }



}
