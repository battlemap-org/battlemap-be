package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreQuestResponseDto {
    private String storeName;
    private List<QuestDetailDto> quests;
    private String message;
}
