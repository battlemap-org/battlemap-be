// src/main/java/org/battlemap/battlemapbe/league/dto/TerritoryDto.java
package org.battlemap.battlemapbe.league.dto;

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
}
