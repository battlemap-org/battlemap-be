package org.battlemap.battlemapbe.dto.league;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaderboardResponseDto {
    private int rank;
    private String nickname;
    private int totalPoints;
}
