package org.battlemap.battlemapbe.dto.league;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DongLeaderboardResponse {
    private String districtName;
    private List<Player> top3;
    private MyRank me;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Player {
        private String name;
        private Long point;
        private String userColorCode;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyRank {
        private int rank;
        private String name;
        private Long point;
        private String userColorCode;
    }
}