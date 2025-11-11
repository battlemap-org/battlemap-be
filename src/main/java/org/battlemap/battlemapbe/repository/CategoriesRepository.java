package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    Optional<Categories> findByCategoryCode(String categoryCode);
}
