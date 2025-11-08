package org.battlemap.battlemapbe.dto.league;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DongLeaderboardResponse {

    // ex) "역곡동"
    private String districtName;

    private List<Player> players;

    @Getter
    @AllArgsConstructor
    public static class Player {
        // Users.name 사용
        private String name;
        // 해당 동에서 획득한 총 포인트 (SUM(rewardPoint))
        private Long point;
    }
}
