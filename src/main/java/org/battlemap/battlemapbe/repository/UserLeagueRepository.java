package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLeagueRepository extends JpaRepository<UserLeagues, Long> {
    Optional<UserLeagues> findByUsers(Users users);
}
