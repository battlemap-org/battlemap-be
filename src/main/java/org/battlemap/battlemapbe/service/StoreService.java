package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.DongsRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final DongsRepository dongsRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    //선택한 시/동, 카테고리 기준으로 카카오 API에서 매장 조회
    // @param cityName  예: "부천시"
    //@param regionName 예: "역곡동"
    //@param category  예: "FD6", "CE7", "AD5", "CULTURE"

    public Map<String, Object> getStoresByRegionAndCategory(String loginId, String cityName, String regionName, String category) {
        // 사용자 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        String fullName = cityName + " " + regionName;

        // Dongs에서 좌표 가져오기 (시+동 기준)
        Dongs dong = dongsRepository
                .findByCities_CityNameAndDongName(cityName, regionName)
                // 혹시 초기 데이터가 "부천시" 고정이고 cityName이 다르게 들어오는 경우를 대비해 fallback
                .or(() -> dongsRepository.findByDongName(regionName))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 지역입니다: " + fullName));

        List<Map<String, Object>> mergedStores = new ArrayList<>();

        // “문화·체험” 복합 카테고리 처리
        if ("CULTURE".equalsIgnoreCase(category)) {

            // 1) 카테고리 코드 기반 (예: 문화시설 CT1)
            List<String> cultureCategories = List.of("CT1");
            for (String cat : cultureCategories) {
                mergedStores.addAll(fetchStoresByCategory(dong, regionName, cat));
            }

            // 2) 키워드 기반 검색 병합
            List<String> keywords = List.of(
                    "공방", "도예", "도예공방", "가죽공방", "캔들공방", "플라워공방", "레진공예", "목공체험",
                    "쿠킹클래스", "베이킹클래스", "원데이클래스",
                    "체험", "실내체험", "VR체험", "방탈출카페", "보드게임카페", "키즈카페", "만화카페",
                    "스크린골프", "스크린야구", "클라이밍", "탁구장", "사격장",
                    "영화관", "공연장", "미술관", "박물관", "전시관", "갤러리",
                    "문화센터", "전통체험", "한옥체험", "문화마을",
                    "캠핑장", "정원", "수목원", "어린이체험관", "실내놀이터"
            );
            for (String keyword : keywords) {
                mergedStores.addAll(fetchStoresByKeyword(dong, regionName, keyword));
            }

        } else {
            // 일반 카테고리 (FD6, CE7, AD5 등)
            mergedStores.addAll(fetchStoresByCategory(dong, regionName, category));
        }

        // id 기준 중복 제거
        Set<String> seen = new HashSet<>();
        List<Map<String, Object>> uniqueStores = mergedStores.stream()
                .filter(store -> {
                    Object id = store.get("id");
                    return id != null && seen.add(id.toString());
                })
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("region", fullName);
        result.put("category", category);
        result.put("count", uniqueStores.size());
        result.put("stores", uniqueStores);

        System.out.println("[" + fullName + "] " + category + " 수집 완료: " + uniqueStores.size() + "개");
        return result;
    }

    // 카카오 카테고리 검색 (category_group_code 사용)
    private List<Map<String, Object>> fetchStoresByCategory(Dongs dong, String regionName, String categoryCode) {
        double baseLat = safe(dong.getLatitude());
        double baseLng = safe(dong.getLongitude());

        double[][] offsets = {
                {0.0, 0.0},
                {0.004, 0.0}, {-0.004, 0.0},
                {0.0, 0.004}, {0.0, -0.004},
                {0.004, 0.004}, {0.004, -0.004},
                {-0.004, 0.004}, {-0.004, -0.004}
        };

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Set<String> uniqueIds = new HashSet<>();
        List<Map<String, Object>> results = new ArrayList<>();

        for (double[] offset : offsets) {
            double lat = baseLat + offset[0];
            double lng = baseLng + offset[1];
            int page = 1;
            boolean isEnd = false;

            while (!isEnd) {
                String url = String.format(
                        "https://dapi.kakao.com/v2/local/search/category.json" +
                                "?category_group_code=%s&x=%f&y=%f&radius=3000&size=15&page=%d",
                        categoryCode, lng, lat, page
                );

                try {
                    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                    Map<String, Object> body = response.getBody();
                    if (body == null || !body.containsKey("documents"))
                        break;

                    List<Map<String, Object>> docs = (List<Map<String, Object>>) body.get("documents");
                    if (docs.isEmpty())
                        break;

                    for (Map<String, Object> doc : docs) {
                        String address = (String) doc.get("address_name");
                        String id = (String) doc.get("id");

                        if (address != null && address.contains(regionName) && id != null && uniqueIds.add(id)) {
                            results.add(convertDocToStore(doc));
                        }
                    }

                    Map<String, Object> meta = (Map<String, Object>) body.get("meta");
                    isEnd = meta != null && Boolean.TRUE.equals(meta.get("is_end"));
                    page++;

                    Thread.sleep(200);
                } catch (Exception e) {
                    System.out.println("⚠ 카카오 API 오류 (" + categoryCode + "): " + e.getMessage());
                    break;
                }
            }
        }

        System.out.println("[" + regionName + "] " + categoryCode + " 수집: " + results.size() + "개");
        return results;
    }

    // 키워드 기반 검색

    private List<Map<String, Object>> fetchStoresByKeyword(Dongs dong, String regionName, String keyword) {
        double lat = safe(dong.getLatitude());
        double lng = safe(dong.getLongitude());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> uniqueIds = new HashSet<>();

        for (int page = 1; page <= 3; page++) {
            String url = String.format(
                    "https://dapi.kakao.com/v2/local/search/keyword.json" +
                            "?query=%s&x=%f&y=%f&radius=3000&page=%d&size=15",
                    keyword, lng, lat, page
            );

            try {
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                Map<String, Object> body = response.getBody();
                if (body == null || !body.containsKey("documents"))
                    break;

                List<Map<String, Object>> docs = (List<Map<String, Object>>) body.get("documents");
                if (docs.isEmpty())
                    break;

                for (Map<String, Object> doc : docs) {
                    String address = (String) doc.get("address_name");
                    String id = (String) doc.get("id");

                    if (address != null && address.contains(regionName) && id != null && uniqueIds.add(id)) {
                        results.add(convertDocToStore(doc));
                    }
                }

                Map<String, Object> meta = (Map<String, Object>) body.get("meta");
                boolean isEnd = meta != null && Boolean.TRUE.equals(meta.get("is_end"));
                if (isEnd)
                    break;

                Thread.sleep(150);
            } catch (Exception e) {
                System.out.println("⚠ 카카오 키워드 검색 오류 (" + keyword + "): " + e.getMessage());
                break;
            }
        }

        // 디버그 로그
        if (!results.isEmpty()) {
            System.out.println("[" + regionName + "] 키워드 '" + keyword + "' 수집: " + results.size() + "개");
        }

        return results;
    }

    private Map<String, Object> convertDocToStore(Map<String, Object> doc) {
        Map<String, Object> store = new LinkedHashMap<>();
        store.put("id", doc.get("id"));
        store.put("place_name", doc.get("place_name"));
        store.put("address_name", doc.get("address_name"));
        store.put("phone", doc.get("phone"));
        store.put("place_url", doc.get("place_url"));
        store.put("x", doc.get("x"));
        store.put("y", doc.get("y"));
        return store;
    }

    private double safe(Double v) {
        if (v == null) throw new IllegalStateException("해당 동에 좌표가 설정되지 않았습니다.");
        return v;
    }
}