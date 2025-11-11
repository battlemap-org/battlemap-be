package org.battlemap.battlemapbe.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Cities;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.repository.DongsRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DongDataInitializer {

    private final DongsRepository dongsRepository;

    @PostConstruct
    public void initDongData() {

        if (dongsRepository.count() > 0) {
            System.out.println("✅ Dongs 데이터 이미 존재함. 초기화 생략");
            return;
        }

        System.out.println("🟢 Dongs 테이블이 비어 있습니다. 부천시 & 동 데이터 자동 등록합니다.");

        // 부천시 (cascade 덕분에 Dongs 저장 시 함께 저장됨)
        Cities bucheon = Cities.builder()
                .cityName("부천시")
                .cityLeague("부천리그")
                .build();

        List<Dongs> dongs = List.of(
                Dongs.builder().dongName("고강동").latitude(37.5295501272997).longitude(126.812115826118).cities(bucheon).build(),
                Dongs.builder().dongName("괴안동").latitude(37.4783719195825).longitude(126.806921315937).cities(bucheon).build(),
                Dongs.builder().dongName("계수동").latitude(37.4652118751889).longitude(126.80808534087).cities(bucheon).build(),
                Dongs.builder().dongName("내동").latitude(37.5250599717814).longitude(126.787783292364).cities(bucheon).build(),
                Dongs.builder().dongName("대장동").latitude(37.5420606337393).longitude(126.775549618468).cities(bucheon).build(),
                Dongs.builder().dongName("도당동").latitude(37.516268049592).longitude(126.785924784297).cities(bucheon).build(),
                Dongs.builder().dongName("범박동").latitude(37.4705881439793).longitude(126.80972848791).cities(bucheon).build(),
                Dongs.builder().dongName("삼정동").latitude(37.5241721607673).longitude(126.767154981449).cities(bucheon).build(),
                Dongs.builder().dongName("상동").latitude(37.4949021135947).longitude(126.760804751664).cities(bucheon).build(),
                Dongs.builder().dongName("소사동").latitude(37.4858944080458).longitude(126.794168231381).cities(bucheon).build(),
                Dongs.builder().dongName("소사본동").latitude(37.4762277086984).longitude(126.792162031052).cities(bucheon).build(),
                Dongs.builder().dongName("송내동").latitude(37.4839506166476).longitude(126.756068319829).cities(bucheon).build(),
                Dongs.builder().dongName("심곡동").latitude(37.4911478996836).longitude(126.783857099779).cities(bucheon).build(),
                Dongs.builder().dongName("심곡본동").latitude(37.4807108086724).longitude(126.780094113587).cities(bucheon).build(),
                Dongs.builder().dongName("약대동").latitude(37.5132817149291).longitude(126.771283379886).cities(bucheon).build(),
                Dongs.builder().dongName("여월동").latitude(37.5128190218343).longitude(126.798213723457).cities(bucheon).build(),
                Dongs.builder().dongName("역곡동").latitude(37.489286049949).longitude(126.816680853404).cities(bucheon).build(),
                Dongs.builder().dongName("옥길동").latitude(37.4669933411314).longitude(126.82266846235).cities(bucheon).build(),
                Dongs.builder().dongName("오정동").latitude(37.5212213610479).longitude(126.794361199142).cities(bucheon).build(),
                Dongs.builder().dongName("원미동").latitude(37.4945152063197).longitude(126.788474674442).cities(bucheon).build(),
                Dongs.builder().dongName("원종동").latitude(37.5259636814997).longitude(126.805278257373).cities(bucheon).build(),
                Dongs.builder().dongName("중동").latitude(37.5047739250295).longitude(126.766191321289).cities(bucheon).build(),
                Dongs.builder().dongName("춘의동").latitude(37.5026880955634).longitude(126.785094754648).cities(bucheon).build(),
                Dongs.builder().dongName("작동").latitude(37.513291343602).longitude(126.815987741765).cities(bucheon).build()
        );

        dongsRepository.saveAll(dongs);

        System.out.println("✅ 부천시 & 동 24개 자동 등록 완료");
    }
}
