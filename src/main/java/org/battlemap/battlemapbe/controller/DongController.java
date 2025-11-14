package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.repository.DongsRepository;
import org.battlemap.battlemapbe.dto.DongResponseDto;
import org.battlemap.battlemapbe.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class DongController {

    private final DongsRepository dongsRepository;
    private final CategoryService categoryService;

    // 부천시 동 목록 조회
    @GetMapping("/{cityName}/dongs")
    public ResponseEntity<List<DongResponseDto>> getDongsByCity(
            Authentication authentication,
            @PathVariable String cityName
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        List<DongResponseDto> dongs = dongsRepository.findByCities_CityName(cityName)
                .stream()
                .map(DongResponseDto::from)
                .toList();

        return ResponseEntity.ok(dongs);
    }

    // 선택한 동의 카테고리 목록 조회
    @GetMapping("/{cityName}/dongs/{dongName}/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories(
            Authentication authentication,
            @PathVariable String cityName,
            @PathVariable String dongName
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        List<Map<String, String>> categories =
                categoryService.getCategoriesByDong(cityName, dongName);

        return ResponseEntity.ok(categories);
    }
}
