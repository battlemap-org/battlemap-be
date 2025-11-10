package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Leagues;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLeaguesRepository extends JpaRepository<UserLeagues, Long> {

    // 🔹 특정 리그의 참가자들을 leaguePoint 기준으로 정렬 (리더보드용)
    List<UserLeagues> findByLeaguesOrderByLeaguePointDesc(Leagues leagues);

    // 🔹 특정 유저 + 특정 리그 조합 조회 (퀘스트 성공 시 사용)
    Optional<UserLeagues> findByUsersAndLeagues(Users users, Leagues leagues);
}
