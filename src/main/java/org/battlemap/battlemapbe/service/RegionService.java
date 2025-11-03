package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.entity.Region;
import org.battlemap.battlemapbe.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    // 카카오 api 활용, 행정 좌표 DB에 저장
    public void saveRegionsFromKakao(List<String> regionNames) {
        for (String name : regionNames) {
            try {
                // 안전하게 URL 인코딩 처리
                String query = URLEncoder.encode("경기도 " + name, StandardCharsets.UTF_8);
                String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + query;

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", kakaoApiKey);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, Map.class
                );

                if (response.getBody() != null) {
                    List<Map<String, Object>> documents =
                            (List<Map<String, Object>>) response.getBody().get("documents");

                    if (documents != null && !documents.isEmpty()) {
                        Map<String, Object> doc = documents.get(0);
                        Map<String, Object> address = (Map<String, Object>) doc.get("address");

                        if (address != null) {
                            double latitude = Double.parseDouble((String) address.get("y"));
                            double longitude = Double.parseDouble((String) address.get("x"));

                            Region region = Region.builder()
                                    .name(name)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .build();

                            regionRepository.save(region);
                            System.out.println(" 저장 완료: " + name + " (" + latitude + ", " + longitude + ")");
                        } else {
                            System.out.println("⚠ 주소 데이터 없음: " + name);
                        }
                    } else {
                        System.out.println("⚠ 좌표 없음: " + name);
                    }
                }
            } catch (Exception e) {
                System.err.println(" [" + name + "] 카카오 API 호출 실패: " + e.getMessage());
            }
        }
    }

    // 전체 지역 조회
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    // 특정 시/군명으로 조회 (예: 부천시)
    public List<Region> getRegionsByCity(String cityName) {
        return regionRepository.findByNameContaining(cityName);
    }
}
