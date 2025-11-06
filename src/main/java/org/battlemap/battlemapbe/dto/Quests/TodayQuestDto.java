package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.battlemap.battlemapbe.model.mapping.TodayQuests;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayQuestDto {
    private Long todayQuestId;
    private String todayContent;
    private Integer todayPoint;

    // 오늘의 퀘스트 dto
    public static TodayQuestDto from(TodayQuests todayQuests) {
        return TodayQuestDto.builder()
                .todayQuestId(todayQuests.getTodayQuestId())
                .todayContent(todayQuests.getTodayContent())
                .todayPoint(todayQuests.getTodayPoint())
                .build();
    }
}
