package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayQuestGenerateResponseDto {
    private TodayQuestDto questData;
    private String message;
}

