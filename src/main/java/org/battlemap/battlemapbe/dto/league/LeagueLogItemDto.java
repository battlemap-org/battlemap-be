package org.battlemap.battlemapbe.dto.league;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueLogItemDto {
    private long tsMillis;     // 타임스탬프(밀리초)
    private String actor;      // 팀/유저명
    private String action;     // CAPTURE / ASSIST / CAPTURE_FAIL ...
    private String detail;     // 설명

    public static LeagueLogItemDto of(long tsMillis, String actor, String action, String detail) {
        return new LeagueLogItemDto(tsMillis, actor, action, detail);
    }
}
