package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.repository.CategoriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoriesRepository categoriesRepository;

    public List<Map<String, String>> getCategoriesByDong(String cityName, String dongName) {
        // 지금은 cityName/dongName 안 쓰지만, 나중에 필터링 로직 넣을 수 있음
        return categoriesRepository.findAll().stream()
                .map(c -> Map.of(
                        "name", c.getCategoryName(),
                        "code", c.getCategoryCode()
                ))
                .toList();
    }
}
