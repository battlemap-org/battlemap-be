package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeagueScoringService {

    private final NamedParameterJdbcTemplate jdbc;

    @Transactional
    public int addScore(String areaId, String seasonId, String userId, int delta) {
        // 1) UPSERT: 복합 PK(user_id, region_id, season_id) 충돌 시 누적
        String upsert = """
            INSERT INTO league_points (user_id, region_id, season_id, base_point)
            VALUES (:uid, :area, :season, :delta)
            ON DUPLICATE KEY UPDATE
                base_point = league_points.base_point + VALUES(base_point),
                updated_at = CURRENT_TIMESTAMP(6)
            """;
        var p = new MapSqlParameterSource()
                .addValue("uid", Long.parseLong(userId)) // Principal.getName()이 숫자 문자열이면 그대로 파싱
                .addValue("area", areaId)
                .addValue("season", seasonId)
                .addValue("delta", delta);
        jdbc.update(upsert, p);

        // 2) 현재 점수 조회 후 리턴
        String sel = """
            SELECT base_point FROM league_points
            WHERE user_id = :uid AND region_id = :area AND season_id = :season
            """;
        Integer score = jdbc.queryForObject(sel, p, Integer.class);
        return score == null ? 0 : score;
    }
}
