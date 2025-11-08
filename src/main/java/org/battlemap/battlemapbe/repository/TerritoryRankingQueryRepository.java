package org.battlemap.battlemapbe.repository;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.league.dto.TerritoryTeamRankDto;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TerritoryRankingQueryRepository {

    private final EntityManager em;

    // territoryId = dong_id, team = users(id,name) 라고 가정
    public List<TerritoryTeamRankDto> findTeamRankingByTerritory(Long territoryId, int topN) {
        String sql = """
            SELECT
                ROW_NUMBER() OVER (ORDER BY SUM(uop.occupy_points) DESC) AS rnk,
                u.id   AS teamId,
                u.name AS teamName,
                SUM(uop.occupy_points) AS score
            FROM useroccupypoints uop
            JOIN users u ON uop.user_id = u.id
            WHERE uop.dong_id = :territoryId
            GROUP BY u.id, u.name
            ORDER BY score DESC
            LIMIT :topN
            """;

        Query q = em.createNativeQuery(sql)
                .setParameter("territoryId", territoryId)
                .setParameter("topN", topN);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();

        List<TerritoryTeamRankDto> result = new ArrayList<>();
        for (Object[] r : rows) {
            int rank = ((Number) r[0]).intValue();
            Long teamId = ((Number) r[1]).longValue();
            String teamName = (String) r[2];
            long score = ((Number) r[3]).longValue();
            result.add(new TerritoryTeamRankDto(rank, teamId, teamName, score));
        }
        return result;
    }
}
