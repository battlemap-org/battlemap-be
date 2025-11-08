package org.battlemap.battlemapbe.dto.league;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerritoryDto {
    private String name;
    private String code;
    private Long polygonId;
    private Double latitude;   // 추가
    private Double longitude;  // 추가
}