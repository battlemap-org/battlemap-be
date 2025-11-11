package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
}
