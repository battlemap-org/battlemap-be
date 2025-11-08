package org.battlemap.battlemapbe.dto.league;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueRankingRowDto {
    private int rank;
    private String team;
    private int score;

    public static LeagueRankingRowDto of(int rank, String team, int score) {
        return new LeagueRankingRowDto(rank, team, score);
    }
}
