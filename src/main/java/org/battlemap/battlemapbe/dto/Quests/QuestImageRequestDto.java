package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestImageRequestDto {
    private String imageUrl; // 사용자가 제출한 이미지 URL
}
