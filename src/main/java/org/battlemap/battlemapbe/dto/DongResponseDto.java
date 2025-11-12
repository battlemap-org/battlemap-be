package org.battlemap.battlemapbe.dto;

import lombok.Builder;
import lombok.Getter;
import org.battlemap.battlemapbe.model.Dongs;

@Getter
@Builder
public class DongResponseDto {

    private Long dongId;
    private String dongName;
    private Double latitude;
    private Double longitude;

    public static DongResponseDto from(Dongs d) {
        return DongResponseDto.builder()
                .dongId(d.getDongId())
                .dongName(d.getDongName())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .build();
    }
}
