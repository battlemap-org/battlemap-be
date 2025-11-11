package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.Quests.StoreQuestResponseDto;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.Stores;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.CategoryRepository;
import org.battlemap.battlemapbe.repository.DongRepository;
import org.battlemap.battlemapbe.repository.QuestsRepository;
import org.battlemap.battlemapbe.repository.StoreRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoreQuestService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DongRepository dongRepository;
    private final CategoryRepository categoryRepository;
    private final QuestService questService;
    private final QuestsRepository questsRepository;

    // 가게별 퀘스트 생성 로직
    @Transactional
    public StoreQuestResponseDto handleStoreClick(
            String loginId,
            String storeId, // Kakao Place ID
            Long dongId,
            Long categoryId,
            Map<String, Object> storeInfo // 카카오 검색 결과 (가게 상세 정보)
    ) {
        // 사용자 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 가게 정보 유효성 검증 (클라이언트에서 전달된 정보 사용)
        if (storeInfo == null || !storeInfo.containsKey("place_name")) {
            throw new CustomException("STORE_INFO_INVALID", "클라이언트로부터 받은 가게 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        String storeName = (String) storeInfo.get("place_name");
        String address = (String) storeInfo.get("address_name");

        // Stores에서 Kakao ID(storeId) 기준으로 가게 정보 조회
        Stores existingStore = storeRepository.findByKakaoPlaceId(storeId).orElse(null);

        // 가게가 DB에 존재할 경우
        if (existingStore != null) {
            // 해당 가게에 연결된 퀘스트가 이미 생성되었는지 확인
            boolean questsExist = questsRepository.existsByStores(existingStore);

            if (questsExist) {
                // 이미 퀘스트가 생성된 경우, 재 생성하지 않고 빈 퀘스트 리스트와 함께 이미 생성되었음을 알리는 메시지 반환
                return new StoreQuestResponseDto(
                        existingStore.getStoreName(),
                        new ArrayList<>(),
                        "이미 이 가게에 대한 퀘스트가 생성되었으므로, 기존 퀘스트를 플레이해주세요."
                );
            }
        }

        // 기존 가게가 없거나 (existingStore == null)
        // 기존 가게는 있지만 퀘스트가 생성되지 않은 경우 (questsExist == false)
        Stores storeToUse = existingStore;

        if (storeToUse == null) {
            // DB에 없으면 새로 저장하는 기존 로직
            Dongs dong = dongRepository.findById(dongId)
                    .orElseThrow(() -> new CustomException("DONG_NOT_FOUND", "존재하지 않는 동입니다.", HttpStatus.NOT_FOUND));
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CustomException("CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND));

            Stores newStore = Stores.builder()
                    .storeName(storeName)
                    .dongs(dong)
                    .categories(category)
                    .kakaoPlaceId(storeId) // 카카오 플레이스 ID 저장
                    .address(address) // 주소 저장
                    .build();
            storeToUse = storeRepository.save(newStore);
        }

        // 퀘스트 생성 (4개)
        return questService.createRandomQuestForStore(loginId, storeToUse.getStoreId());
    }
}