package org.battlemap.battlemapbe.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestDto;
import org.battlemap.battlemapbe.dto.Quests.TodayQuestGenerateResponseDto;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.mapping.Categories;
import org.battlemap.battlemapbe.model.mapping.TodayQuests;
import org.battlemap.battlemapbe.repository.CategoryRepository;
import org.battlemap.battlemapbe.repository.DongsRepository;
import org.battlemap.battlemapbe.repository.TodayQuestRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
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

    // 오늘의 퀘스트 템플릿
    private static final List<String> QUEST_TEMPLATES = List.of(
            "%s 방문 인증 시 보너스 %d 포인트 지급!",
            "%s 카테고리 방문 인증 시 보너스 %d 포인트 지급!"
    );

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

        int templateIndex = random.nextInt(QUEST_TEMPLATES.size());
        Integer bonusPoint = BONUS_POINTS.get(random.nextInt(BONUS_POINTS.size()));

        String questContent;

        if (templateIndex == 0) {
            questContent = String.format(QUEST_TEMPLATES.get(0), randomDong.getDongName(), bonusPoint);
        } else {
            List<Categories> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                throw new CustomException("CATEGORY_NOT_FOUND", "유효한 카테고리가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Categories randomCategory = categories.get(random.nextInt(categories.size()));

            questContent = String.format(QUEST_TEMPLATES.get(1), randomCategory.getCategoryName(), bonusPoint);
        }

        TodayQuests newQuest = TodayQuests.builder()
                .todayContent(questContent)
                .todayPoint(bonusPoint)
                .dongs(randomDong)
                .build();

        todayQuestRepository.save(newQuest);

        return TodayQuestDto.from(newQuest);
        }
}
