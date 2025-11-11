package org.battlemap.battlemapbe.response;

import lombok.Builder;
import lombok.Getter;
import org.battlemap.battlemapbe.model.Dongs;

@Getter
@Builder
public class DongResponse {

    private Long dongId;
    private String dongName;
    private Double latitude;
    private Double longitude;

    public static DongResponse from(Dongs d) {
        return DongResponse.builder()
                .dongId(d.getDongId())
                .dongName(d.getDongName())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .build();
    }
}
