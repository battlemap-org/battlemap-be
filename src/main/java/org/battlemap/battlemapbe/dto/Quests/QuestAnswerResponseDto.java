package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestAnswerResponseDto {
    private boolean isCorrect;
    private Integer rewardPoint;

    public static QuestAnswerResponseDto from(boolean isCorrect, Integer earnedPoint) {
        return QuestAnswerResponseDto.builder()
                .isCorrect(isCorrect)
                .rewardPoint(earnedPoint)
                .build();
    }
}