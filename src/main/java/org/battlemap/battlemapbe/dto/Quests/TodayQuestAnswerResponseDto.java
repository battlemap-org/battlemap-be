package org.battlemap.battlemapbe.dto.Quests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodayQuestAnswerResponseDto {
    private boolean isCorrect;
    private int reward;
    private String message;

    public TodayQuestAnswerResponseDto(boolean isCorrect, int reward, String message) {
        this.isCorrect = isCorrect;
        this.reward = reward;
        this.message = message;
    }

    // 응답 dto
    public static TodayQuestAnswerResponseDto from(boolean isCorrect, int reward) {
        String msg = isCorrect ? "퀘스트 인증에 성공했습니다!" : "퀘스트 인증에 실패했습니다.";
        return new TodayQuestAnswerResponseDto(isCorrect, reward, msg);
    }
}

