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
        return categoriesRepository.findAll().stream()
                .map(c -> Map.of(
                        "name", c.getCategoryName(),
                        "code", c.getCategoryCode()
                ))
                .toList();
    }
}
