// src/main/java/org/battlemap/battlemapbe/repository/LeagueRepository.java
package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.entity.League;
import org.battlemap.battlemapbe.league.model.LeagueStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeagueRepository extends JpaRepository<League, Long> {
    List<League> findByStatusOrderByIdDesc(LeagueStatus status);

    // 전체 정렬 조회는 서비스에서 Sort로 처리해도 됨
    default List<League> findAllDesc() {
        return findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}
