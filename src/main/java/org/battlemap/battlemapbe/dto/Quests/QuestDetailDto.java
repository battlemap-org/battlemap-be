package org.battlemap.battlemapbe.dto.Quests;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestDetailDto {
    private Long questId;
    private Integer number;
    private String content;
    private Integer score;
}
