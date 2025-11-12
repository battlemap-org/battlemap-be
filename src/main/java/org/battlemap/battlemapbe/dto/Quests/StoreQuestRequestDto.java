package org.battlemap.battlemapbe.dto.Quests;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreQuestRequestDto {
    private Long dongId;
    private Long categoryId;

    private Map<String, Object> storeInfo;
}
