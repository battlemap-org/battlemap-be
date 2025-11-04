package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.QuestDto;
import org.battlemap.battlemapbe.dto.Quests.QuestWithStoreDto;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestDto;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.QuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    // 가게별 퀘스트 목록 조회 API
    @GetMapping("/{storeId}/stores")
    public ResponseEntity<ApiResponse<List<QuestWithStoreDto>>> getStoreQuests(@PathVariable Long storeId) {
        if (storeId == null) {
            throw new CustomException("QUEST_404", "요청이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        List<QuestWithStoreDto> quests = questService.getQuestsByStoreId(storeId);

        return ResponseEntity.ok(ApiResponse.success(quests, 200));
    }

    // 가게별 퀘스트 풀이 화면 - 조회 API
    @GetMapping("/{questId}/solve")
    public ResponseEntity<ApiResponse<QuestDto>> getSolveQuests(@PathVariable Long questId) {
        QuestDto quests = questService.getQuestsByQuestId(questId);

        return ResponseEntity.ok(ApiResponse.success(quests, 200));
    }

    // 오늘의 퀘스트 조회
    @GetMapping("/{todayQuestId}/today")
    public ResponseEntity<ApiResponse<TodayQuestDto>> getTodayQuests(@PathVariable Long todayQuestId) {
        TodayQuestDto todayQuests = questService.getTodayQuestsByQuestId(todayQuestId);

        return ResponseEntity.ok(ApiResponse.success(todayQuests, 200));
    }
}