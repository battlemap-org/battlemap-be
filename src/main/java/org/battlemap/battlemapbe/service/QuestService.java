package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.QuestDto;
import org.battlemap.battlemapbe.dto.Quests.QuestWithStoreDto;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestDto;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Stores;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.battlemap.battlemapbe.repository.QuestsRepository;
import org.battlemap.battlemapbe.repository.StoreRepository;
import org.battlemap.battlemapbe.repository.TodayQuestRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestsRepository questsRepository;
    private final StoreRepository storeRepository;
    private final TodayQuestRepository todayQuestRepository;
    private final UserRepository userRepository;


    // 퀘스트 목록 조회
    public List<QuestWithStoreDto> getQuestsByStoreId(String loginId, Long storeId) {
        // 사용자 검증
        userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 가게 존재 여부 확인
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() ->
                        new CustomException("STORE_404", "해당 가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 퀘스트 목록 조회 및 DTO 변환
        List<Quests> quests = questsRepository.findByStores_StoreId(storeId);

        return quests.stream()
                .map(quest -> QuestWithStoreDto.of(quest, store))
                .collect(Collectors.toList());
    }

    // 퀘스트 풀이 화면 조회
    public QuestDto getQuestsByQuestId(String loginId, Long questId) {
        // 사용자 검증
        userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 퀘스트 존재 여부 확인
        Quests quest = questsRepository.findById(questId)
                .orElseThrow(() ->
                        new CustomException("QUEST_404", "해당 퀘스트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return QuestDto.from(quest);
    }

    // 오늘의 퀘스트 조회
    public TodayQuestDto getTodayQuestsByQuestId(String loginId, Long todayQuestId) {
        // 사용자 검증
        userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        TodayQuests todayQuest = todayQuestRepository.findById(todayQuestId)
                // 퀘스트가 없는 경우 - 404
                .orElseThrow(() -> new CustomException("QUEST_404", "TodayQuest 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return TodayQuestDto.from(todayQuest);
    }
}
