package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.DongLeaderboardQueryRepository;
import org.battlemap.battlemapbe.repository.LeaguesRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DongLeaderboardService {

    private final DongLeaderboardQueryRepository queryRepository;
    private final UserRepository userRepository;
    private final LeaguesRepository leaguesRepository; // 현재 시즌 조회용

    // 기본 색상 코드 (사용자를 찾지 못하거나 코드가 없을 경우 대비)
    private static final String DEFAULT_COLOR = "#D3D3D3";

    @Transactional(readOnly = true)
    public DongLeaderboardResponse getDongLeaderboard(String loginId, String dongName) {

        // 로그인 유저 확인
        Users me = userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));
        String myName = me.getName();
        String myColorCode = me.getUserColorCode(); // 1. 로그인 사용자 본인 색상 조회

        // 현재 진행 중인 리그(시즌) 조회
        LocalDateTime now = LocalDateTime.now();
        var currentLeague = leaguesRepository.findCurrentLeague(now)
                .orElseThrow(() ->
                        new CustomException("LEAGUE_NOT_FOUND", "진행 중인 시즌이 없습니다.", HttpStatus.BAD_REQUEST));

        // 퀘스트 누적 포인트 집계 (닉네임, 포인트만 포함된 DTO 리스트)
        List<DongLeaderboardResponse.Player> allPlayers =
                queryRepository.findDongLeaderboardByDongNameAndPeriod(
                        dongName,
                        currentLeague.getStartDate(),
                        currentLeague.getEndDate()
                );

        // 모든 리더보드 사용자들의 닉네임을 추출
        List<String> playerNames = allPlayers.stream()
                .map(DongLeaderboardResponse.Player::getName)
                .collect(Collectors.toList());

        // 닉네임 리스트를 기반으로 모든 Users 엔티티를 한 번에 조회 (findAllByNameIn 사용)
        List<Users> usersInLeaderboard = userRepository.findAllByNameIn(playerNames);

        // 닉네임을 Key로, 색상 코드를 Value로 하는 Map을 생성 (O(1) 시간 복잡도로 색상 조회 가능)
        Map<String, String> colorMap = usersInLeaderboard.stream()
                .collect(Collectors.toMap(
                        Users::getName,
                        user -> user.getUserColorCode() != null ? user.getUserColorCode() : DEFAULT_COLOR,
                        (existing, replacement) -> existing // 충돌 시 기존 값 유지 (닉네임은 고유하므로 거의 발생 안 함)
                ));

        // TOP3 + 내 순위 계산 및 색상 할당
        List<DongLeaderboardResponse.Player> top3 = new ArrayList<>();
        int myRank = 0;
        Long myPoint = 0L;

        for (int i = 0; i < allPlayers.size(); i++) {
            DongLeaderboardResponse.Player p = allPlayers.get(i);
            int rank = i + 1;

            // Map에서 색상 코드를 가져와 DTO를 재구성함
            String currentColor = colorMap.getOrDefault(p.getName(), DEFAULT_COLOR);

            // DTO를 색상 코드를 포함하여 재구성 (Player DTO는 name, point, colorCode 필드가 있음)
            DongLeaderboardResponse.Player playerWithColor = new DongLeaderboardResponse.Player(
                    p.getName(),
                    p.getPoint(),
                    currentColor // 색상 코드 포함
            );

            if (i < 3) {
                top3.add(playerWithColor); // TOP3 리스트에 색상이 포함된 DTO 추가
            }

            if (p.getName().equals(myName)) {
                myRank = rank;
                myPoint = p.getPoint();
            }
        }

        // 응답 DTO 구성
        // MyRank DTO에 본인 색상 코드를 추가합니다.
        DongLeaderboardResponse.MyRank meDto =
                new DongLeaderboardResponse.MyRank(myRank, myName, myPoint, myColorCode); // myColorCode 추가

        return DongLeaderboardResponse.builder()
                .districtName(dongName)
                .top3(top3)
                .me(meDto)
                .build();
    }
}