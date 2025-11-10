package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.dto.Quests.QuestCountByDongDto;
import org.battlemap.battlemapbe.dto.region.UserDongPointResponse;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserQuestsRepository extends JpaRepository<UserQuests, Long> {
    Optional<UserQuests> findByUsersAndQuests(Users users, Quests quests);

    // 지역 별 완료 퀘스트 조회
    @Query("SELECT new org.battlemap.battlemapbe.dto.Quests.QuestCountByDongDto(" +
            "s.dongs.dongId, s.dongs.dongName, COUNT(uq)) " +
            "FROM UserQuests uq " +
            "JOIN uq.quests q " +
            "JOIN q.stores s " +
            "WHERE uq.isCompleted = true " +
            "GROUP BY s.dongs.dongId, s.dongs.dongName " +
            "ORDER BY COUNT(uq) DESC")
    List<QuestCountByDongDto> countCompletedByDong();

    @Query("SELECT new org.battlemap.battlemapbe.dto.region.UserDongPointResponse(" +
            "d.dongName, " +
            "COALESCE(SUM(q.rewardPoint), 0), " +
            "COUNT(uq)) " +
            "FROM UserQuests uq " +
            "JOIN uq.users u " +
            "JOIN uq.quests q " +
            "JOIN q.stores s " +
            "JOIN s.dongs d " +
            "JOIN d.cities c " +
            "WHERE u.id = :loginId " +
            "AND uq.isCompleted = true " +
            "AND c.cityName = :cityName " +
            "GROUP BY d.dongId, d.dongName " +
            "ORDER BY d.dongName")
    List<UserDongPointResponse> findDongPointsByLoginIdAndCity(
            @Param("loginId") String loginId,
            @Param("cityName") String cityName
    );


}
