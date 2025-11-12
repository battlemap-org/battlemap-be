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

@Service
@RequiredArgsConstructor
public class DongLeaderboardService {

    private final DongLeaderboardQueryRepository queryRepository;
    private final UserRepository userRepository;
    private final LeaguesRepository leaguesRepository; // 🔹 현재 시즌 조회용

    @Transactional(readOnly = true)
    public DongLeaderboardResponse getDongLeaderboard(String loginId, String dongName) {

        // ✅ 1. 로그인 유저 확인
        Users me = userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));
        String myName = me.getName();

        // ✅ 2. 현재 진행 중인 리그(시즌) 조회
        LocalDateTime now = LocalDateTime.now();
        var currentLeague = leaguesRepository.findCurrentLeague(now)
                .orElseThrow(() ->
                        new CustomException("LEAGUE_NOT_FOUND", "진행 중인 시즌이 없습니다.", HttpStatus.BAD_REQUEST));

        // ✅ 3. 현재 시즌(startDate~endDate) 기간 내의 퀘스트만 집계
        List<DongLeaderboardResponse.Player> all =
                queryRepository.findDongLeaderboardByDongNameAndPeriod(
                        dongName,
                        currentLeague.getStartDate(),
                        currentLeague.getEndDate()
                );

        // ✅ 4. TOP3 + 내 순위 계산
        List<DongLeaderboardResponse.Player> top3 = new ArrayList<>();
        int myRank = 0;
        Long myPoint = 0L;

        for (int i = 0; i < all.size(); i++) {
            DongLeaderboardResponse.Player p = all.get(i);
            int rank = i + 1;

            if (i < 3) {
                top3.add(p);
            }

            if (p.getName().equals(myName)) {
                myRank = rank;
                myPoint = p.getPoint();
            }
        }

        // ✅ 5. 응답 DTO 구성
        DongLeaderboardResponse.MyRank meDto =
                new DongLeaderboardResponse.MyRank(myRank, myName, myPoint);

        return DongLeaderboardResponse.builder()
                .districtName(dongName)
                .top3(top3)
                .me(meDto)
                .build();
    }
}
