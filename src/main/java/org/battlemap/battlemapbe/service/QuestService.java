package org.battlemap.battlemapbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.*;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Stores;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.battlemap.battlemapbe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestsRepository questsRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserQuestsRepository userQuestsRepository;
    private final UserLeagueRepository userLeagueRepository;

    // 퀘스트 템플릿
    private static final List<String> QUEST_TEMPLATES = List.of(
            "가게 앞에서 인증샷 찍기(간판이 나오게)",
            "%s에서 가장 비싼 메뉴는?",
            "%s에서 가장 싼 메뉴는? (사이드 포함)",
            "%s의 시그니처 메뉴는?",
            "%s의 인기 메뉴는?",
            "%s에는 주차장이 있나요? (예/아니오)",
            "%s에서 가장 가까운 지하철 역은?"
    );

    // 퀘스트 정답 템플릿 (임시)
    private static final List<String> QUEST_ANSWERS = List.of(
            "인증 필요",
            "비싼 메뉴",
            "싼 메뉴",
            "라떼",
            "토마토파스타",
            "예",
            "홍대입구역"
    );

    // 퀘스트 리워드 포인트 템플릿
    private static final List<Integer> REWARD_POINTS = List.of(50, 100, 150, 200);

    // 퀘스트 개수 상수 정의
    private static final int QUEST_COUNT_TO_GENERATE = 4;

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

    // 퀘스트 답변 제출
    public QuestAnswerResponseDto QuestAnswer(Long questId, String loginId, String userAnswerContent) {
        // 사용자 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 퀘스트 존재 여부 확인
        Quests quest = questsRepository.findById(questId)
                .orElseThrow(() -> new CustomException("QUEST_404", "TodayQuest 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 정답 여부 판단
        boolean isCorrect = quest.getAnswer().equalsIgnoreCase(userAnswerContent.trim());
        int reward = isCorrect ? quest.getRewardPoint() : 0;

        // 기존 기록 조회 or 새로 생성
        UserQuests userQuest = userQuestsRepository.findByUsersAndQuests(user, quest)
                .orElse(UserQuests.builder()
                        .users(user)
                        .quests(quest)
                        .userAnswer("")      // 기본값 설정 (NULL 방지)
                        .isCompleted(false)  // 기본값 설정
                        .build());

        // isCompleted =1 이라면
        if (Boolean.TRUE.equals(userQuest.getIsCompleted())) {
            throw new CustomException("QUEST_ALREADY_COMPLETED", "이미 완료한 퀘스트입니다.", HttpStatus.BAD_REQUEST);
        }

        // 답변 및 완료 여부 업데이트
        userQuest.setUserAnswer(userAnswerContent);
        userQuest.setIsCompleted(isCorrect);
        //정답일 경우 완료시간 추가
        if (isCorrect) {
            userQuest.setCompletedAt(java.time.LocalDateTime.now());
        }
        userQuestsRepository.save(userQuest);

        // 정답일 경우 포인트 지급
        if (isCorrect) {
            UserLeagues userLeague = userLeagueRepository.findByUsers(user)
                    .orElseThrow(() -> new CustomException("LEAGUE_NOT_FOUND", "해당 사용자의 리그 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

            int currentPoint = userLeague.getLeaguePoint();
            userLeague.setLeaguePoint(currentPoint + reward);
            userLeagueRepository.save(userLeague);
        }

        // 응답 DTO 반환
        return QuestAnswerResponseDto.from(isCorrect, reward);
    }

    // 가게별 퀘스트 생성 (4개)
    public StoreQuestResponseDto createRandomQuestForStore(String loginId, Long storeId) {
        // 사용자 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));

        // 가게 검증
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException("STORE_NOT_FOUND", "존재하지 않는 가게입니다.", HttpStatus.NOT_FOUND));

        Random random = new Random();

        List<QuestDetailDto> questDetails = new ArrayList<>();

        // 템플릿 인덱스 리스트 생성 (0~6)
        List<Integer> templateIndices = new ArrayList<>();
        for (int i = 0; i < QUEST_TEMPLATES.size(); i++) {
            templateIndices.add(i);
        }

        // 인덱스 리스트를 랜덤하게 섞음
        Collections.shuffle(templateIndices);

        // 섞인 리스트에서 4개만 선택하여 퀘스트 생성
        for (int i = 0; i < QUEST_COUNT_TO_GENERATE; i++) {
            int templateIndex = templateIndices.get(i);

            String template = QUEST_TEMPLATES.get(templateIndex);
            String questContent;

            if (template.contains("%s")) {
                questContent = String.format(template, store.getStoreName());
            } else {
                questContent = template;
            }

            String answer = QUEST_ANSWERS.get(templateIndex);
            Integer rewardPoint = REWARD_POINTS.get(random.nextInt(REWARD_POINTS.size()));

            // Quests 생성 및 저장
            Quests quest = Quests.builder()
                    .questNumber(i + 1) // 퀘스트 번호는 1, 2, 3, 4
                    .questContent(questContent)
                    .answer(answer)
                    .rewardPoint(rewardPoint)
                    .stores(store)
                    .build();

            questsRepository.save(quest);

            // 사용자와 퀘스트 연결 (UserQuests 생성)
            UserQuests userQuest = UserQuests.builder()
                    .users(user)
                    .quests(quest)
                    .isCompleted(false)
                    .build();

            userQuestsRepository.save(userQuest);

            questDetails.add(QuestDetailDto.builder()
                    .questId(quest.getQuestId())
                    .number(quest.getQuestNumber())
                    .content(questContent)
                    .score(rewardPoint)
                    .build());
        }

        return new StoreQuestResponseDto(
                store.getStoreName(),
                questDetails,
                "4개의 퀘스트가 성공적으로 생성되었습니다!"
        );
    }
}
