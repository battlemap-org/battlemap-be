package org.battlemap.battlemapbe.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.entity.Region;
import org.battlemap.battlemapbe.repository.RegionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionDataInitializer {

    private final RegionRepository regionRepository;

    // 서버 시작 시 자동 실행
    @PostConstruct
    public void initRegionData() {

        if (regionRepository.count() > 0) {
            System.out.println(" Region 데이터 이미 존재함. 초기화 생략");
            return;
        }

        System.out.println(" Region 테이블이 비어 있습니다. 기본 부천시 법정동 데이터를 자동 등록합니다.");

        List<Region> regions = List.of(
                new Region(null, "부천시 고강동", 37.5295501272997, 126.812115826118),
                new Region(null, "부천시 괴안동", 37.4783719195825, 126.806921315937),
                new Region(null, "부천시 계수동", 37.4652118751889, 126.80808534087),
                new Region(null, "부천시 대장동", 37.5420606337393, 126.775549618468),
                new Region(null, "부천시 도당동", 37.516268049592, 126.785924784297),
                new Region(null, "부천시 범박동", 37.4705881439793, 126.80972848791),
                new Region(null, "부천시 삼정동", 37.5241721607673, 126.767154981449),
                new Region(null, "부천시 상동", 37.4949021135947, 126.760804751664),
                new Region(null, "부천시 소사본동", 37.4762277086984, 126.792162031052),
                new Region(null, "부천시 소사동", 37.4858944080458, 126.794168231381),
                new Region(null, "부천시 송내동", 37.4839506166476, 126.756068319829),
                new Region(null, "부천시 심곡동", 37.4911478996836, 126.783857099779),
                new Region(null, "부천시 심곡본동", 37.4807108086724, 126.780094113587),
                new Region(null, "부천시 약대동", 37.5132817149291, 126.771283379886),
                new Region(null, "부천시 여월동", 37.5128190218343, 126.798213723457),
                new Region(null, "부천시 역곡동", 37.489286049949, 126.816680853404),
                new Region(null, "부천시 옥길동", 37.4669933411314, 126.82266846235),
                new Region(null, "부천시 원미동", 37.4945152063197, 126.788474674442),
                new Region(null, "부천시 원종동", 37.5259636814997, 126.805278257373),
                new Region(null, "부천시 춘의동", 37.5026880955634, 126.785094754648),
                new Region(null, "부천시 작동", 37.513291343602, 126.815987741765)
        );

        regionRepository.saveAll(regions);
        System.out.println(" 부천시 법정동 21개 자동 등록 완료!");
    }
}
