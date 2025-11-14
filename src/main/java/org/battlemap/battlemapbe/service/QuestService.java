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

    // 퀘스트 정답 템플릿 (AI 호출 실패 시 Fallback 용)
    private static final List<String> QUEST_ANSWERS = List.of(
            "인증 필요",
            "가장 비싼 메뉴와 그 가격 (예: 'XX', 00000원)",
            "가장 싼 메뉴와 그 가격 (사이드 메뉴 포함, 예: 'XX', 0000원)",
            "시그니처 메뉴 (메뉴 이름만 간결하게)",
            "인기 메뉴 1가지 (메뉴 이름만 간결하게)",
            "주차 가능 여부 (예/아니오/정보 없음 중 하나로만 대답)",
            "가장 가까운 지하철 역 이름 (예: 역곡역)"
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

        // 중복 정답 허용
        // DB에 저장된 답 (예: "계란김밥,보석김밥")을 쉼표로 분리
        List<String> correctAnswers = Arrays.asList(quest.getAnswer().split(","));

        // 사용자의 답변이 정답 목록 중 하나와 일치하는지 확인
        boolean isCorrect = correctAnswers.stream()
                .anyMatch(answer -> answer.trim().equalsIgnoreCase(userAnswerContent.trim()));

        int reward = isCorrect ? quest.getRewardPoint() : 0;

        // 기존 기록 조회 or 새로 생성
        UserQuests userQuest = userQuestsRepository.findByUsersAndQuests(user, quest)
                .orElse(UserQuests.builder()
                        .users(user)
                        .quests(quest)
                        .userAnswer("")
                        .isCompleted(false)
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

        // AI 프롬프트를 OCR(텍스트 추출)로 변경
        String prompt = "Extract all visible text from the main sign in this image. Respond with only the extracted text.";

        // Gemini API 호출 (URL -> Base64 변환)
        boolean isCorrect = callGeminiVisionApiFromUrl(prompt, imageUrl, storeName);
        int reward = isCorrect ? quest.getRewardPoint() : 0;

        String authMessage = isCorrect ? "AI 인증 완료" : "AI 인증 실패";

        // UserQuests 기록 업데이트 (userAnswer에 이미지 URL 저장)
        userQuest.setUserAnswer(imageUrl);
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

    // Gemini Vision API 호출 (URL 다운로드 후 Base64로 변환)

    private boolean callGeminiVisionApiFromUrl(String prompt, String imageUrl, String storeName) {

        // URL에서 이미지 다운로드
        byte[] imageBytes;
        String mimeType;

        try {
            // User-Agent 헤더 설정 (일부 호스팅 사이트 403 방지)
            HttpHeaders downloadHeaders = new HttpHeaders();
            downloadHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            HttpEntity<String> downloadEntity = new HttpEntity<>(downloadHeaders);

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(imageUrl, HttpMethod.GET, downloadEntity, byte[].class);

            if (!responseEntity.hasBody() || responseEntity.getBody() == null) {
                throw new CustomException("IMAGE_DOWNLOAD_FAILED", "이미지 URL에서 빈 파일을 다운로드했습니다.", HttpStatus.BAD_REQUEST);
            }

            imageBytes = responseEntity.getBody();
            MediaType mediaType = responseEntity.getHeaders().getContentType();

            // MimeType 동적 감지
            if (mediaType != null && mediaType.getType().startsWith("image")) {
                mimeType = mediaType.toString();
            } else {
                mimeType = "image/jpeg"; // 기본값
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

        // API 요청 Body (inline_data 사용)
        GeminiPart textPart = new GeminiPart(prompt);
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

            // 응답 파싱
            if (response.getBody() != null &&
                    response.getBody().getCandidates() != null &&
                    !response.getBody().getCandidates().isEmpty() &&
                    response.getBody().getCandidates().get(0).getContent() != null &&
                    !response.getBody().getCandidates().get(0).getContent().getParts().isEmpty()) {

                String aiResponse = response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
                if (aiResponse == null) return false;

                // 공백 제거
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

    // Gemini 텍스트 생성 API 호출 (Google Search 연동)
    private String callGeminiTextApi(String storeName, String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // Google Search를 사용하도록 tools 설정
        GeminiTool tool = new GeminiTool(new HashMap<>());

        // AI가 간결하게 답변하도록 프롬프트 수정 (중복 정답 포함)
        String fullPrompt = String.format("'%s' 가게에 대한 다음 질문에 간결하게 답해줘: '%s'. " +
                        "설명이나 부연 없이 오직 답만 말해줘. (예: 50,000원 또는 예 또는 역곡역). " +
                        "만약 가격이 같은 메뉴 등 정답이 여러 개라면 쉼표(,)로 구분해줘. (예: 계란김밥,보석김밥)",
                storeName, prompt);

        GeminiPart textPart = new GeminiPart(fullPrompt);
        GeminiContent content = new GeminiContent("user", List.of(textPart));

        // 텍스트 생성용 Payload
        GeminiTextRequest payload = new GeminiTextRequest(List.of(content), List.of(tool));

        HttpEntity<GeminiTextRequest> entity = new HttpEntity<>(payload, headers);

        try {
            String apiUrl = geminiApiUrl + geminiApiKey;
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            // 응답 파싱
            if (response.getBody() != null &&
                    response.getBody().getCandidates() != null &&
                    !response.getBody().getCandidates().isEmpty() &&
                    response.getBody().getCandidates().get(0).getContent() != null &&
                    !response.getBody().getCandidates().get(0).getContent().getParts().isEmpty()) {

                String aiResponse = response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
                return aiResponse.trim();
            }
            return null; // AI가 답변을 생성하지 못함

        } catch (Exception e) {
            System.err.println("Gemini 텍스트 API 호출 실패: " + e.getMessage());
            return null; // API 호출 실패
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
        Collections.shuffle(templateIndices);

        for (int i = 0; i < QUEST_COUNT_TO_GENERATE; i++) {
            int templateIndex = templateIndices.get(i);

            String template = QUEST_TEMPLATES.get(templateIndex);
            String questContent;

            if (template.contains("%s")) {
                questContent = String.format(template, store.getStoreName());
            } else {
                questContent = template;
            }

            String answerTemplate = QUEST_ANSWERS.get(templateIndex);
            String answer;

            if ("인증 필요".equals(answerTemplate)) {
                answer = "인증 필요";
            } else {
                // "인증 필요"가 아니면, AI를 호출하여 실제 정답을 생성
                // AI에게 전송할 프롬프트
                String aiGeneratedAnswer = callGeminiTextApi(store.getStoreName(), answerTemplate);

                // AI 호출 실패 시 임시 템플릿 사용
                answer = (aiGeneratedAnswer != null) ? aiGeneratedAnswer : answerTemplate;
            }

            Integer rewardPoint = REWARD_POINTS.get(random.nextInt(REWARD_POINTS.size()));

            Quests quest = Quests.builder()
                    .questNumber(i + 1)
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

    // gemini vision dto

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

    // GeminiFileData (URL)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiFileData {
        @JsonProperty("mime_type")
        private String mimeType;
        @JsonProperty("file_uri")
        private String fileUri;
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

        @JsonProperty("file_data")
        private GeminiFileData fileData; // URL용

        @JsonProperty("inline_data")
        private GeminiInlineData inlineData; // Base64용

        // 텍스트용 생성자
        public GeminiPart(String text) {
            this.text = text;
        }

        // 이미지 Base64용 생성자
        public GeminiPart(GeminiInlineData inlineData) {
            this.inlineData = inlineData;
        }

        // 이미지 URL용 생성자
        public GeminiPart(GeminiFileData fileData) {
            this.fileData = fileData;
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

    // --- Gemini Text (Google Search) DTOs ---

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiTextRequest {
        private List<GeminiContent> contents;
        private List<GeminiTool> tools;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiTool {
        @JsonProperty("google_search")
        private Object googleSearch;
    }
}

