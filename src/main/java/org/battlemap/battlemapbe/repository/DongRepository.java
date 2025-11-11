package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Dongs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DongRepository extends JpaRepository<Dongs, Long> {
}
