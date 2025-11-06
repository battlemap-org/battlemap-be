package org.battlemap.battlemapbe.league.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueRequestDto {
    private String leagueName;
    private String region;
    private String dong;              // "역곡동"
    private LocalDateTime startAt;    // 선택
    private LocalDateTime endAt;      // 선택
}
