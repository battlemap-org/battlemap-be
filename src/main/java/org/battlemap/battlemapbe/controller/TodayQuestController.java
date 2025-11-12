package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestAnswerResponseDto;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestDto;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestGenerateResponseDto;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.TodayQuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class TodayQuestController {

    private final TodayQuestService todayQuestService;

    // 오늘의 퀘스트 조회
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<TodayQuestDto>> getTodayQuest(
            Authentication authentication
    ) {
        String loginId = authentication.getName();

        TodayQuestDto todayQuest = todayQuestService.getDailyQuest(loginId);

        return ResponseEntity.ok(ApiResponse.success(todayQuest, 200));
    }

    // 오늘의 퀘스트 생성 (하루에 1개)
    @PostMapping("/today/generate")
    public ResponseEntity<ApiResponse<TodayQuestGenerateResponseDto>> generateTodayQuest() {
        // 인증 필요 없음. (서버 내부 로직)
        TodayQuestGenerateResponseDto response = todayQuestService.generateDailyQuestIfNoneExists();
        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }

    // 오늘의 퀘스트 인증
    @PostMapping("/{todayQuestId}/answers-today")
    public ResponseEntity<ApiResponse<TodayQuestAnswerResponseDto>> completeTodayQuest(
            Authentication authentication,
            @PathVariable("todayQuestId") Long todayQuestId
    ) {
        String loginId = authentication.getName();

        TodayQuestAnswerResponseDto response = todayQuestService.completeTodayQuest(
                loginId,
                todayQuestId
        );
        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }
}
