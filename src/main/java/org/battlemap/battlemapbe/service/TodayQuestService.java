package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.dto.Quests.TodayQuestAnswerResponseDto;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.battlemap.battlemapbe.model.mapping.UserQuests;
import org.battlemap.battlemapbe.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestDto;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestGenerateResponseDto;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.mapping.Categories;
import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TodayQuestService {

    private final UserRepository userRepository;
    private final TodayQuestRepository todayQuestRepository;
    private final CategoryRepository categoryRepository;
    private final DongsRepository dongsRepository;
    private final UserQuestsRepository userQuestsRepository;
    private final UserLeagueRepository userLeagueRepository;

    // 오늘의 퀘스트 템플릿
    private static final String QUEST_TEMPLATE = "%s에서 %s 방문 인증 시 보너스 %d 포인트 지급!";

    // 리워드 포인트 템플릿
    private static final List<Integer> BONUS_POINTS = List.of(30, 50, 70, 100);

    // 오늘의 퀘스트 조회
    @Transactional(readOnly = true)
    public TodayQuestDto getDailyQuest(String loginId) {
        // 사용자 검증
        userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 오늘 날짜의 퀘스트를 조회
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        TodayQuests todayQuest = todayQuestRepository.findFirstByCreatedAtBetween(startOfDay, endOfDay)
                .orElseThrow(() -> new CustomException("QUEST_404", "오늘의 퀘스트가 아직 생성되지 않았습니다.", HttpStatus.NOT_FOUND));

        // DTO 반환
        return TodayQuestDto.from(todayQuest);
    }

    // 오늘의 퀘스트 생성
    @Transactional
    public TodayQuestGenerateResponseDto generateDailyQuestIfNoneExists() {
        // 오늘 생성된 퀘스트가 있는지 확인
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Optional<TodayQuests> existingQuest = todayQuestRepository.findFirstByCreatedAtBetween(startOfDay, endOfDay);

        // 퀘스트가 이미 존재하면 메시지를 포함하여 반환
        if (existingQuest.isPresent()) {
            return new TodayQuestGenerateResponseDto(
                    TodayQuestDto.from(existingQuest.get()),
                    "이미 퀘스트가 생성되었습니다."
            );
        }

        TodayQuestDto newQuestDto = createNewDailyQuest();

        return new TodayQuestGenerateResponseDto(
                newQuestDto,
                "오늘의 퀘스트가 성공적으로 생성되었습니다."
        );
    }

    private TodayQuestDto createNewDailyQuest() {
        Random random = new Random();

        // 랜덤 동 선택
        List<Dongs> dongs = dongsRepository.findAll();
        if (dongs.isEmpty()) {
            throw new CustomException("DONG_NOT_FOUND", "데이터베이스에 등록된 동이 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Dongs randomDong = dongs.get(random.nextInt(dongs.size()));

        // 랜덤 카테고리 선택
        List<Categories> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new CustomException("CATEGORY_NOT_FOUND", "유효한 카테고리가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Categories randomCategory = categories.get(random.nextInt(categories.size()));

        // 랜덤 포인트 선택
        Integer bonusPoint = BONUS_POINTS.get(random.nextInt(BONUS_POINTS.size()));

        // 템플릿으로 퀘스트 내용 조합
        String questContent = String.format(
                QUEST_TEMPLATE,
                randomDong.getDongName(),
                randomCategory.getCategoryName(),
                bonusPoint
        );

        // TodayQuest 엔티티 생성
        TodayQuests newQuest = TodayQuests.builder()
                .todayContent(questContent)
                .todayPoint(bonusPoint)
                .dongs(randomDong)
                .categories(randomCategory)
                .build();

        todayQuestRepository.save(newQuest);

        return TodayQuestDto.from(newQuest);
    }

    // 오늘의 퀘스트 인증
    @Transactional
    public TodayQuestAnswerResponseDto completeTodayQuest(String loginId, Long todayQuestId) {

        // 사용자 및 오늘의 퀘스트 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        TodayQuests todayQuest = todayQuestRepository.findById(todayQuestId)
                .orElseThrow(() -> new CustomException("QUEST_404", "오늘의 퀘스트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 이미 이 오늘의 퀘스트를 완료했는지 확인
        Optional<UserQuests> existingUserTodayQuest = userQuestsRepository.findByUsersAndTodayQuests(user, todayQuest);

        if (existingUserTodayQuest.isPresent() && Boolean.TRUE.equals(existingUserTodayQuest.get().getIsCompleted())) {
            throw new CustomException("QUEST_ALREADY_COMPLETED", "이미 완료한 퀘스트입니다.", HttpStatus.BAD_REQUEST);
        }

        // 인증 로직: 오늘 완료한 '가게 퀘스트'가 조건에 맞는지 확인
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        boolean isCriteriaMet = userQuestsRepository.hasCompletedStoreQuestMatchingCriteria(
                user,
                todayQuest.getDongs(),      // 오늘의 퀘스트가 요구하는 '동'
                todayQuest.getCategories(), // 오늘의 퀘스트가 요구하는 '카테고리'
                startOfDay,
                endOfDay
        );

        // 인증 실패 처리
        if (!isCriteriaMet) {
            return new TodayQuestAnswerResponseDto(false, 0, "인증 실패: 오늘 완료한 오늘의 퀘스트에 해당하는 가게 퀘스트가 없습니다.");
        }

        // 인증 성공: '오늘의 퀘스트'를 완료 처리
        int reward = todayQuest.getTodayPoint();

        // UserQuests 기록 생성 또는 업데이트 (오늘의 퀘스트에 대해)
        UserQuests userQuestLog = existingUserTodayQuest.orElse(UserQuests.builder()
                .users(user)
                .todayQuests(todayQuest)
                .isCompleted(false)
                .userAnswer("인증 완료")
                .build());

        userQuestLog.setIsCompleted(true);
        userQuestLog.setCompletedAt(LocalDateTime.now()); // 완료 시각 기록
        userQuestsRepository.save(userQuestLog);

        // 리그 포인트 추가
        UserLeagues userLeague = userLeagueRepository.findByUsers(user)
                .orElseThrow(() -> new CustomException("LEAGUE_NOT_FOUND", "해당 사용자의 리그 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        userLeague.setLeaguePoint(userLeague.getLeaguePoint() + reward);
        userLeagueRepository.save(userLeague);

        return new TodayQuestAnswerResponseDto(true, reward, "인증 성공!");
    }
}