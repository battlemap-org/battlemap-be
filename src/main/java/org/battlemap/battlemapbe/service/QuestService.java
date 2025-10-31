package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.QuestDto;
import org.battlemap.battlemapbe.dto.Quests.QuestWithStoreDto;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Stores;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.QuestsRepository;
import org.battlemap.battlemapbe.repository.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestsRepository questsRepository;
    private final StoreRepository storeRepository;


    // 가게별 퀘스트 목록 조회
    public List<QuestWithStoreDto> getQuestsByStoreId(Long storeId) {
        // 가게 존재 여부 확인
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException("STORE_404", "해당 가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<Quests> quests = questsRepository.findByStores_StoreId(storeId);

        // 퀘스트가 없는 경우 - null 반환
        // DTO 리스트로 변환하여 반환
        return quests.stream()
                .map(quest -> QuestWithStoreDto.of(quest, store))
                .collect(Collectors.toList());
    }

    // quest 풀이 화면 - 조회
    public QuestDto getQuestsByQuestId(Long questId) {
        Quests quest = questsRepository.findById(questId)
                // 퀘스트가 없는 경우 - 404
                .orElseThrow(() -> new CustomException("QUEST_404", "Quest 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return QuestDto.from(quest);
    }
}