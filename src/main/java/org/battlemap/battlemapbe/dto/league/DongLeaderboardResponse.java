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

    // ex) "역곡동"
    private String districtName;

    // 상단에 보여줄 TOP 3
    private List<Player> top3;

    // 항상 보여줄 내 정보 (TOP3 안에 있어도 별도로 표시)
    private MyRank me;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Player {
        private String name;   // Users.name
        private Long point;    // 해당 동 누적 포인트
        private String userColorCode; // 리더보드 목록의 사용자 색상
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyRank {
        private int rank;      // 0이면 랭킹 없음
        private String name;
        private Long point;    // null 또는 0이면 점수 없음
        private String userColorCode; // 본인 색상
    }
}