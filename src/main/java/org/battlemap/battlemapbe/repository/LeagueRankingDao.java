package org.battlemap.battlemapbe.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LeagueRankingDao {

    private final NamedParameterJdbcTemplate jdbc;

    private static final String SQL_TOP = """
        SELECT u.name AS nickname, lp.base_point
        FROM league_points lp
        JOIN users u ON u.user_id = lp.user_id
        WHERE lp.region_id = :area AND lp.season_id = :season
        ORDER BY lp.base_point DESC
        LIMIT :limit
        """;

    public List<Row> findTop(String areaId, String seasonId, int limit) {
        var p = new MapSqlParameterSource()
                .addValue("area", areaId)
                .addValue("season", seasonId)
                .addValue("limit", limit);
        return jdbc.query(SQL_TOP, p, (rs, i) ->
                new Row(rs.getString("nickname"), rs.getInt("base_point"))
        );
    }

    public record Row(String nickname, int basePoint) {}
}
