package org.battlemap.battlemapbe.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.mapping.Categories;
import org.battlemap.battlemapbe.repository.CategoriesRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final CategoriesRepository categoriesRepository;

    @PostConstruct
    public void init() {
        if (categoriesRepository.count() > 0) {
            return;
        }

        List<Categories> defaults = List.of(
                Categories.builder()
                        .categoryName("식당")
                        .categoryCode("FD6")
                        .build(),
                Categories.builder()
                        .categoryName("카페")
                        .categoryCode("CE7")
                        .build(),
                Categories.builder()
                        .categoryName("문화·체험")
                        .categoryCode("CULTURE")
                        .build(),
                Categories.builder()
                        .categoryName("숙박")
                        .categoryCode("AD5")
                        .build()
        );

        categoriesRepository.saveAll(defaults);
        System.out.println("✅ 기본 카테고리(FD6, CE7, CULTURE, AD5) 초기화 완료");
    }
}
