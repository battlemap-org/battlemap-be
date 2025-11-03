package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.QuestDto;
import org.battlemap.battlemapbe.dto.Quests.QuestWithStoreDto;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.response.ApiResponse;
import org.battlemap.battlemapbe.service.QuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    // 퀘스트 목록 조회
    @GetMapping("/{storeId}/stores")
    public ResponseEntity<ApiResponse<List<QuestWithStoreDto>>> getStoreQuests(
            Authentication authentication,
            @PathVariable Long storeId
    ) {
        if (storeId == null) {
            throw new CustomException("QUEST_404", "요청이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        String loginId = authentication.getName();
        List<QuestWithStoreDto> quests = questService.getQuestsByStoreId(loginId, storeId);
        return ResponseEntity.ok(ApiResponse.success(quests, 200));
    }

    // 퀘스트 풀이 화면 조회
    @GetMapping("/{questId}/solve")
    public ResponseEntity<ApiResponse<QuestDto>> getSolveQuests(
            Authentication authentication,
            @PathVariable Long questId
    ) {
        String loginId = authentication.getName();
        QuestDto quest = questService.getQuestsByQuestId(loginId, questId);
        return ResponseEntity.ok(ApiResponse.success(quest, 200));
    }
}
