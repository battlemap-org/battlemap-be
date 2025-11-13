package org.battlemap.battlemapbe.dto.league;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponseDto {
    private int rank;
    private String nickname;
    private int totalPoints;
    private String userColorCode;
}
