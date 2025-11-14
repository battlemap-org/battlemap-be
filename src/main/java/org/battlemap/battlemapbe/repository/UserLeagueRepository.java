package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Leagues;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLeagueRepository extends JpaRepository<UserLeagues, Long> {

    // 특정 리그의 유저들 리그포인트 순 정렬 (리더보드용)
    List<UserLeagues> findByLeaguesOrderByLeaguePointDesc(Leagues leagues);

    // 유저 1명에 대한 UserLeagues
    Optional<UserLeagues> findByUsers(Users users);

    // 유저 + 리그 조합으로 조회 (여러 시즌/리그 있을 걸 대비한 보다 안전한 버전)
    Optional<UserLeagues> findByUsersAndLeagues(Users users, Leagues leagues);
}
