package org.battlemap.battlemapbe.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.transaction.annotation.Transactional;
import lombok.*;
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
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import java.util.Collections;

import java.util.Base64;

import java.util.*;
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
    private final LeagueService leagueService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

    // 퀘스트 답변 제출 (텍스트)
    @Transactional
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
            // 현재 진행 중인 리그(시즌)를 찾음
            var currentLeague = leagueService.getCurrentLeagueOrThrow();

            // 사용자 + 현재 리그 조합으로 UserLeagues 조회 및 없으면 새로 생성
            UserLeagues userLeague = userLeagueRepository.findByUsersAndLeagues(user, currentLeague)
                    .orElseGet(() -> {
                        // 해당 시즌 첫 퀘스트이므로 새로 생성
                        UserLeagues newUserLeague = UserLeagues.builder()
                                .users(user)
                                .leagues(currentLeague)
                                .leaguePoint(0)
                                .userRank(0) // 추가: user_rank에 0으로 기본값 설정
                                .build();
                        return userLeagueRepository.save(newUserLeague);
                    });

            // 리그 포인트 업데이트 및 저장
            int currentPoint = userLeague.getLeaguePoint();
            userLeague.setLeaguePoint(currentPoint + reward);
            userLeagueRepository.save(userLeague);
        }

        return QuestAnswerResponseDto.from(isCorrect, reward, userAnswerContent);
    }

    // 퀘스트 답변 제출 (이미지)
    @Transactional
    public QuestAnswerResponseDto completeImageQuest(String loginId, Long questId, String imageUrl) {
        // 사용자 및 퀘스트 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Quests quest = questsRepository.findById(questId)
                .orElseThrow(() -> new CustomException("QUEST_404", "해당 퀘스트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 퀘스트 타입 검증 ("인증 필요" 퀘스트여야 함)
        if (!quest.getAnswer().equals("인증 필요")) {
            throw new CustomException("INVALID_QUEST_TYPE", "이 퀘스트는 이미지 인증 퀘스트가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        // 이미 완료했는지 검증
        UserQuests userQuest = userQuestsRepository.findByUsersAndQuests(user, quest)
                .orElse(UserQuests.builder()
                        .users(user)
                        .quests(quest)
                        .isCompleted(false)
                        .build());

        if (Boolean.TRUE.equals(userQuest.getIsCompleted())) {
            throw new CustomException("QUEST_ALREADY_COMPLETED", "이미 완료한 퀘스트입니다.", HttpStatus.BAD_REQUEST);
        }

        // AI 이미지 검증
        String storeName = quest.getStores().getStoreName();

        // Gemini API 프롬프트 생성
        String prompt = "Extract all visible text from the main sign in this image. Respond with only the extracted text.";

        // emini API 호출 (URL -> Base64 변환)
        boolean isCorrect = callGeminiVisionApiFromUrl(prompt, imageUrl, storeName);
        int reward = isCorrect ? quest.getRewardPoint() : 0;

        String authMessage = isCorrect ? "AI 인증 완료" : "AI 인증 실패";

        // UserQuests 기록 업데이트
        userQuest.setUserAnswer(imageUrl); // userAnswer에 이미지 URL 저장
        userQuest.setIsCompleted(isCorrect);
        if (isCorrect) {
            userQuest.setCompletedAt(java.time.LocalDateTime.now());
        }
        userQuestsRepository.save(userQuest);

        // 포인트 지급 (성공 시)
        if (isCorrect) {
            var currentLeague = leagueService.getCurrentLeagueOrThrow();
            UserLeagues userLeague = userLeagueRepository.findByUsersAndLeagues(user, currentLeague)
                    .orElseGet(() -> {
                        UserLeagues newUserLeague = UserLeagues.builder()
                                .users(user)
                                .leagues(currentLeague)
                                .leaguePoint(0)
                                .userRank(0)
                                .build();
                        return userLeagueRepository.save(newUserLeague);
                    });

            userLeague.setLeaguePoint(userLeague.getLeaguePoint() + reward);
            userLeagueRepository.save(userLeague);
        }

        return QuestAnswerResponseDto.from(isCorrect, reward, authMessage);
    }

    //Gemini Vision API 호출 (URL 다운로드 후 Base64로 변환)
    private boolean callGeminiVisionApiFromUrl(String prompt, String imageUrl, String storeName) {

        // URL에서 이미지 다운로드
        byte[] imageBytes;
        String mimeType;

        try {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(imageUrl, HttpMethod.GET, null, byte[].class);

            if (!responseEntity.hasBody() || responseEntity.getBody() == null) {
                throw new CustomException("IMAGE_DOWNLOAD_FAILED", "이미지 URL에서 빈 파일을 다운로드했습니다.", HttpStatus.BAD_REQUEST);
            }

            imageBytes = responseEntity.getBody();

            // Content-Type 헤더에서 실제 MimeType 추출
            MediaType mediaType = responseEntity.getHeaders().getContentType();
            if (mediaType != null) {
                mimeType = mediaType.toString();
            } else {
                // 헤더가 없는 경우, 기본값(jpeg)으로 설정 (실패 가능성 있음)
                mimeType = "image/jpeg";
            }

        } catch (Exception e) {
            System.err.println("이미지 다운로드 실패 (URL: " + imageUrl + "): " + e.getMessage());
            return false;
        }

        // Base64 인코딩
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Gemini API 요청 (inline_data 사용)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // API 요청 Body 생성 (동적 MimeType 사용)
        GeminiPart textPart = new GeminiPart(prompt);
        // 'mimeType' 변수 사용
        GeminiPart imagePart = new GeminiPart(new GeminiInlineData(mimeType, base64Image));
        GeminiContent content = new GeminiContent("user", List.of(textPart, imagePart));
        GeminiRequest payload = new GeminiRequest(List.of(content));

        HttpEntity<GeminiRequest> entity = new HttpEntity<>(payload, headers);

        try {
            String apiUrl = geminiApiUrl + geminiApiKey;

            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            // 응답 파싱 (기존과 동일)
            if (response.getBody() != null &&
                    response.getBody().getCandidates() != null &&
                    !response.getBody().getCandidates().isEmpty() &&
                    response.getBody().getCandidates().get(0).getContent() != null &&
                    !response.getBody().getCandidates().get(0).getContent().getParts().isEmpty()) {

                String aiResponse = response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
                if (aiResponse == null) return false;

                String aiText = aiResponse.replaceAll("\\s+", "");
                String storeNameText = storeName.replaceAll("\\s+", "");

                // AI 디버깅 로그
                System.out.println("===== AI Debug Log =====");
                System.out.println("Store Name: " + storeNameText);
                System.out.println("AI Response: " + aiText);
                System.out.println("Contains Check: " + aiText.contains(storeNameText));

                return aiText.contains(storeNameText);
            }
            return false;

        } catch (Exception e) {
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            return false;
        }
    }

    // 가게별 퀘스트 생성 (4개)
    @Transactional
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
                store.getStoreId(),
                store.getStoreName(),
                questDetails,
                "4개의 퀘스트가 성공적으로 생성되었습니다!"
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiRequest {
        private List<GeminiContent> contents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiContent {
        private String role;
        private List<GeminiPart> parts;
    }

    // GeminiInlineData (Base64)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiInlineData {
        @JsonProperty("mime_type")
        private String mimeType;
        @JsonProperty("data")
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiPart {
        private String text;

        // inlineData
        @JsonProperty("inline_data")
        private GeminiInlineData inlineData;

        // 텍스트용 생성자
        public GeminiPart(String text) {
            this.text = text;
        }

        // 이미지 Base64용 생성자
        public GeminiPart(GeminiInlineData inlineData) {
            this.inlineData = inlineData;
        }
    }

    // Gemini 응답 DTO
    @Data
    @NoArgsConstructor
    public static class GeminiResponse {
        private List<GeminiCandidate> candidates;
    }

    @Data
    @NoArgsConstructor
    public static class GeminiCandidate {
        private GeminiContent content;
    }
}