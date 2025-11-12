package org.battlemap.battlemapbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.battlemap.battlemapbe.model.Dongs;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DongResponseDto {

    private Long dongId;
    private String dongName;

    private Double latitude;
    private Double longitude;

    private Integer mapX;
    private Integer mapY;
    private Integer radius;

    public static DongResponseDto from(Dongs d) {
        return DongResponseDto.builder()
                .dongId(d.getDongId())
                .dongName(d.getDongName())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .mapX(d.getMapX())
                .mapY(d.getMapY())
                .radius(d.getRadius())
                .build();
    }
}
