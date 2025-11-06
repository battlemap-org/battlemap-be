package org.battlemap.battlemapbe.league.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueLogResponseDto {
    private List<LeagueLogItemDto> items;
    private Long nextCursor;   // 다음 페이지 커서(null이면 끝)

    public static LeagueLogResponseDto of(List<LeagueLogItemDto> items, Long nextCursor) {
        return new LeagueLogResponseDto(items, nextCursor);
    }
}
