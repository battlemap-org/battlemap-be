package org.battlemap.battlemapbe.dto.Quests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestAnswerRequestDto {
    private String userAnswerContent; // 사용자가 제출한 답변 내용
}
