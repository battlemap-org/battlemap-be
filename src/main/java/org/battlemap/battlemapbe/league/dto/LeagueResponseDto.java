package org.battlemap.battlemapbe.league.dto;

import lombok.*;
import org.battlemap.battlemapbe.league.model.LeagueStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueResponseDto {

    private Long id;                  // 나중에 DB 붙을 때 사용
    private String leagueName;        // 리그 이름 (예: "부천-역곡동 11월 리그")
    private String region;            // 시/도 (예: "경기")
    private String dong;              // 행정동 (예: "역곡동")
    private LeagueStatus status;      // SCHEDULED / ACTIVE / ENDED
    private LocalDateTime startAt;    // 선택
    private LocalDateTime endAt;      // 선택

    // 목데이터나 단순 생성용 편의 메서드
    public static LeagueResponseDto of(
            Long id,
            String leagueName,
            String region,
            String dong,
            LeagueStatus status,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return LeagueResponseDto.builder()
                .id(id)
                .leagueName(leagueName)
                .region(region)
                .dong(dong)
                .status(status)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
