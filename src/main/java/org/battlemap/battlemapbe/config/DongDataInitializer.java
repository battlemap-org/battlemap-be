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

        System.out.println("🟢 Dongs 테이블이 비어 있습니다. 부천시 & 동 좌표 데이터 자동 등록합니다.");

        Cities bucheon = Cities.builder()
                .cityName("부천시")
                .cityLeague("부천리그")
                .build();

        List<Dongs> dongs = List.of(
                Dongs.builder()
                        .dongName("고강동")
                        .latitude(37.5295501272997).longitude(126.812115826118)
                        .mapX(801).mapY(358).radius(51)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("괴안동")
                        .latitude(37.4783719195825).longitude(126.806921315937)
                        .mapX(733).mapY(998).radius(46)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("계수동")
                        .latitude(37.4652118751889).longitude(126.80808534087)
                        .mapX(681).mapY(1193).radius(24)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("내동")
                        .latitude(37.5250599717814).longitude(126.787783292364)
                        .mapX(440).mapY(491).radius(28)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("대장동")
                        .latitude(37.5420606337393).longitude(126.775549618468)
                        .mapX(401).mapY(237).radius(47)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("도당동")
                        .latitude(37.516268049592).longitude(126.785924784297)
                        .mapX(468).mapY(593).radius(48)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("범박동")
                        .latitude(37.4705881439793).longitude(126.80972848791)
                        .mapX(725).mapY(1110).radius(38)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("삼정동")
                        .latitude(37.5241721607673).longitude(126.767154981449)
                        .mapX(312).mapY(451).radius(55)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("상동")
                        .latitude(37.4949021135947).longitude(126.760804751664)
                        .mapX(133).mapY(745).radius(53)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("소사동")
                        .latitude(37.4858944080458).longitude(126.794168231381)
                        .mapX(572).mapY(927).radius(31)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("소사본동")
                        .latitude(37.4762277086984).longitude(126.792162031052)
                        .mapX(558).mapY(1078).radius(74)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("송내동")
                        .latitude(37.4839506166476).longitude(126.756068319829)
                        .mapX(257).mapY(966).radius(51)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("심곡동")
                        .latitude(37.4911478996836).longitude(126.783857099779)
                        .mapX(425).mapY(883).radius(36)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("심곡본동")
                        .latitude(37.4807108086724).longitude(126.780094113587)
                        .mapX(418).mapY(1017).radius(47)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("약대동")
                        .latitude(37.5132817149291).longitude(126.771283379886)
                        .mapX(317).mapY(579).radius(21)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("여월동")
                        .latitude(37.5128190218343).longitude(126.798213723457)
                        .mapX(618).mapY(605).radius(50)
                        .cities(bucheon).build(),

                // ✅ 작동 추가
                Dongs.builder()
                        .dongName("작동")
                        .latitude(37.513291343602).longitude(126.815987741765)
                        .mapX(787).mapY(606).radius(58)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("역곡동")
                        .latitude(37.489286049949).longitude(126.816680853404)
                        .mapX(701).mapY(876).radius(75)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("옥길동")
                        .latitude(37.4669933411314).longitude(126.82266846235)
                        .mapX(874).mapY(1163).radius(35)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("오정동")
                        .latitude(37.5212213610479).longitude(126.794361199142)
                        .mapX(463).mapY(360).radius(45)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("원미동")
                        .latitude(37.4945152063197).longitude(126.788474674442)
                        .mapX(551).mapY(817).radius(43)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("원종동")
                        .latitude(37.5259636814997).longitude(126.805278257373)
                        .mapX(687).mapY(409).radius(45)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("중동")
                        .latitude(37.5047739250295).longitude(126.766191321289)
                        .mapX(327).mapY(740).radius(86)
                        .cities(bucheon).build(),

                Dongs.builder()
                        .dongName("춘의동")
                        .latitude(37.5026880955634).longitude(126.785094754648)
                        .mapX(663).mapY(720).radius(40)
                        .cities(bucheon).build()
        );


        dongsRepository.saveAll(dongs);

        System.out.println("✅ 부천시 동 좌표 데이터 등록 완료 (lat/lng + mapX/mapY/radius)");
    }
}
