package org.battlemap.battlemapbe.dto.league;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class TerritoryTeamRankDto {
    private int rank;          // 1,2,3...
    private Long teamId;       // 팀/유저/길드 등 실제 기준에 맞춰 변경
    private String teamName;
    private long score;        // 점수/포인트
}
