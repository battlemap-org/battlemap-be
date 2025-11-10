package org.battlemap.battlemapbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.battlemap.battlemapbe.repository.UserLeaguesRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeagueService {

    private final UserLeaguesRepository userLeaguesRepository;
    private final UserRepository userRepository;

    /**
     * 🔹 이번 시즌 리더보드 + 남은 시간 + 내 시즌 포인트 표시
     * 시즌 종료 시 — 전체 이월 + 상위권 보너스 반영
     */
    public LeagueResponse getMonthlyLeaderboard(String loginId, String cityName) {

        // ✅ 로그인 사용자 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        // ✅ 이번 달 리더보드 조회
        List<Object[]> results = userLeaguesRepository.findCurrentMonthLeaderboard();
        List<LeaderboardResponseDto> leaderboard = new ArrayList<>();

        int rank = 1;
        int myRank = 0;
        int mySeasonPoint = 0;
        String myNickname = user.getName();

        for (Object[] row : results) {
            String nickname = (String) row[0];
            int totalPoints = ((Number) row[1]).intValue();

            leaderboard.add(LeaderboardResponseDto.builder()
                    .rank(rank)
                    .nickname(nickname)
                    .totalPoints(totalPoints)
                    .build());

            if (nickname.equals(myNickname)) {
                myRank = rank;
                mySeasonPoint = totalPoints;
            }

            rank++;
        }

        // ✅ 남은 시즌 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59);
        Duration duration = Duration.between(now, endOfMonth);

        String remaining = String.format("%d일 %d시간 %d분",
                duration.toDays(),
                duration.toHours() % 24,
                duration.toMinutes() % 60
        );

        // ✅ 시즌 종료 시 전체 이월 및 보너스 반영
        if (now.getDayOfMonth() == endOfMonth.getDayOfMonth()) {
            applySeasonBonus(leaderboard);
        }

        return new LeagueResponse(leaderboard, myRank, myNickname, mySeasonPoint, remaining);
    }

    /**
     * 🔹 시즌 종료 시: 유저리그의 leaguePoint 갱신 + 상위권 보너스 반영 + 리셋
     */
    @Transactional
    private void applySeasonBonus(List<LeaderboardResponseDto> leaderboard) {
        for (LeaderboardResponseDto dto : leaderboard) {
            int bonusRate = switch (dto.getRank()) {
                case 1 -> 50;
                case 2, 3 -> 30;
                case 4, 5 -> 10;
                default -> 0;
            };

            userRepository.findByName(dto.getNickname()).ifPresent(u -> {
                // 🔹 유저의 현재 리그 포인트 가져오기
                UserLeagues userLeague = userLeaguesRepository
                        .findByUsers_UserId(u.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("USER_LEAGUE_NOT_FOUND"));

                int basePoints = dto.getTotalPoints();
                int bonusPoints = (basePoints * bonusRate) / 100;
                int totalToAdd = basePoints + bonusPoints;

                // ✅ 유저리그 포인트 업데이트
                userLeague.setLeaguePoint(totalToAdd);
                userLeaguesRepository.save(userLeague);
            });
        }
    }

    /**
     * 🔹 응답 DTO (리더보드 + 내 순위/닉네임 + 남은 시간)
     */
    public record LeagueResponse(
            List<LeaderboardResponseDto> leaderboard,
            int myRank,
            String myNickname,
            int mySeasonPoint,
            String remainingTime
    ) {}
}
