package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.entity.Region;
import org.battlemap.battlemapbe.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class StoreService {

    private final RegionRepository regionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public StoreService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public Map<String, Object> getStoresByRegionAndCategory(String cityName, String regionName, String category) {
        String fullName = cityName + " " + regionName;

        Region region = regionRepository.findByName(fullName)
                .or(() -> regionRepository.findByName(regionName))
                .or(() -> regionRepository.findByName("부천시 " + regionName))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 지역입니다: " + fullName));

        List<Map<String, Object>> mergedStores = new ArrayList<>();

        // “문화·체험” 카테고리는 문화시설 + 키워드 기반 병합
        if (category.equalsIgnoreCase("CULTURE")) {
            List<String> categories = List.of("CT1"); // 문화시설
            for (String cat : categories) {
                mergedStores.addAll(fetchStores(region, regionName, cat));
            }

            // 키워드 기반 검색 추가
            List<String> keywords = List.of(
                    "공방", "도예", "도예공방", "가죽공방", "캔들공방", "플라워공방", "레진공예", "목공체험", "쿠킹클래스", "베이킹클래스", "원데이클래스",
                    "체험", "실내체험", "VR체험", "방탈출카페", "보드게임카페", "키즈카페", "만화카페", "스크린골프", "스크린야구", "클라이밍", "탁구장", "사격장",
                    "영화관", "공연장", "미술관", "박물관", "전시관", "갤러리", "문화센터", "전통체험", "한옥체험", "문화마을", "캠핑장", "정원", "수목원", "어린이체험관", "실내놀이터"
            );
            for (String keyword : keywords) {
                mergedStores.addAll(fetchKeywordStores(region, regionName, keyword));
            }
        } else {
            // 일반 카테고리(FD6, CE7, AD5 등)
            mergedStores.addAll(fetchStores(region, regionName, category));
        }

        // 중복 제거 (id 기준)
        Set<String> seen = new HashSet<>();
        List<Map<String, Object>> uniqueStores = mergedStores.stream()
                .filter(store -> seen.add((String) store.get("id")))
                .toList();

        // 결과 반환
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("region", fullName);
        result.put("category", category);
        result.put("count", uniqueStores.size());
        result.put("stores", uniqueStores);

        System.out.println(" [" + fullName + "] " + category + " 수집 완료: " + uniqueStores.size() + "개");
        return result;
    }

    // 카카오 카테고리 검색
    private List<Map<String, Object>> fetchStores(Region region, String regionName, String category) {
        double baseLat = region.getLatitude();
        double baseLng = region.getLongitude();

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
                        "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=%s&x=%f&y=%f&radius=3000&size=15&page=%d",
                        category, lng, lat, page);

                try {
                    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                    Map<String, Object> body = response.getBody();
                    if (body == null || !body.containsKey("documents")) break;

                    List<Map<String, Object>> docs = (List<Map<String, Object>>) body.get("documents");
                    if (docs.isEmpty()) break;

                    for (Map<String, Object> doc : docs) {
                        String address = (String) doc.get("address_name");
                        String id = (String) doc.get("id");

                        if (address != null && address.contains(regionName) && uniqueIds.add(id)) {
                            results.add(convertDocToStore(doc));
                        }
                    }

                    Map<String, Object> meta = (Map<String, Object>) body.get("meta");
                    isEnd = meta != null && Boolean.TRUE.equals(meta.get("is_end"));
                    page++;
                    Thread.sleep(200);

                } catch (Exception e) {
                    System.out.println("⚠ 카카오 API 오류 (" + category + "): " + e.getMessage());
                    break;
                }
            }
        }

        System.out.println(" [" + regionName + "] " + category + " 수집 완료: " + results.size() + "개");
        return results;
    }

    // 키워드 검색
    private List<Map<String, Object>> fetchKeywordStores(Region region, String regionName, String keyword) {
        double lat = region.getLatitude();
        double lng = region.getLongitude();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> uniqueIds = new HashSet<>();

        for (int page = 1; page <= 3; page++) {
            String url = String.format(
                    "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f&radius=3000&page=%d&size=15",
                    keyword, lng, lat, page);

            try {
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                Map<String, Object> body = response.getBody();
                if (body == null || !body.containsKey("documents")) break;

                List<Map<String, Object>> docs = (List<Map<String, Object>>) body.get("documents");
                if (docs.isEmpty()) break;

                for (Map<String, Object> doc : docs) {
                    String address = (String) doc.get("address_name");
                    String id = (String) doc.get("id");

                    if (address != null && address.contains(regionName) && uniqueIds.add(id)) {
                        results.add(convertDocToStore(doc));
                    }
                }

                Map<String, Object> meta = (Map<String, Object>) body.get("meta");
                boolean isEnd = meta != null && Boolean.TRUE.equals(meta.get("is_end"));
                if (isEnd) break;

                Thread.sleep(150);

            } catch (Exception e) {
                System.out.println("⚠ 카카오 키워드 검색 오류 (" + keyword + "): " + e.getMessage());
            }
        }

        System.out.println(" [" + regionName + "] 키워드 '" + keyword + "' 수집 완료: " + results.size() + "개");
        return results;
    }

    // 공통 변환 함수
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
}
