package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestCountByDongDto {
    private Long dongId;
    private String dongName;
    private Long completedCount;
}
