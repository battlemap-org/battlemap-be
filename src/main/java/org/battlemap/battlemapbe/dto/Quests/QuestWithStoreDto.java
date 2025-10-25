package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Stores;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestWithStoreDto {
    private Long questId;
    private Integer questNumber;
    private Integer rewardPoint;
    private Long storeId;
    private String storeName;

    public static QuestWithStoreDto of(Quests quest, Stores store) {
        return QuestWithStoreDto.builder()
                .questId(quest.getQuestId())
                .questNumber(quest.getQuestNumber())
                .rewardPoint(quest.getRewardPoint())
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .build();
    }
}
