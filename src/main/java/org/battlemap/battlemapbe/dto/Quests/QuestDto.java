package org.battlemap.battlemapbe.dto.Quests;

import lombok.Builder;
import lombok.Getter;
import org.battlemap.battlemapbe.model.Quests;

@Builder
@Getter
public class QuestDto {
    private final Long questId;
    private final Integer questNumber;
    private final String questContent;

    // quest 풀이 화면 dto
    public static QuestDto from(Quests quest) {
        return QuestDto.builder()
                .questId(quest.getQuestId())
                .questNumber(quest.getQuestNumber())
                .questContent(quest.getQuestContent())
                .build();
    }
}