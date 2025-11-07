package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestRepository extends JpaRepository<UserQuests, Long> {
    long countByUsers(Users user);
}
