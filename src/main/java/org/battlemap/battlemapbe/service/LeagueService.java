package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.model.Users;
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
     * 시즌 종료 시 — 상위권에 보너스 포인트 자동 반영
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

            // ✅ 내 닉네임과 일치하면 순위/포인트 저장
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

        // ✅ 시즌 종료 시 보너스 포인트 반영 (DB만 수정, 반환엔 포함 안 함)
        if (now.getDayOfMonth() == endOfMonth.getDayOfMonth()) {
            applySeasonBonus(leaderboard);
        }

        // ✅ 리턴: 내 순위, 닉네임 포함
        return new LeagueResponse(leaderboard, myRank, myNickname, mySeasonPoint, remaining);
    }

    /**
     * 🔹 리그 순위 기반 보너스 지급
     * 1위: +50%, 2~3위: +30%, 4~5위: +10%, 그 외: 0%
     */
    private void applySeasonBonus(List<LeaderboardResponseDto> leaderboard) {
        for (LeaderboardResponseDto dto : leaderboard) {
            int bonusRate = switch (dto.getRank()) {
                case 1 -> 50;
                case 2, 3 -> 30;
                case 4, 5 -> 10;
                default -> 0;
            };

            if (bonusRate == 0) continue;

            int bonusPoints = (dto.getTotalPoints() * bonusRate) / 100;

            // ✅ 닉네임으로 사용자 찾아 포인트(balance)에 반영
            userRepository.findByName(dto.getNickname()).ifPresent(u -> {
                u.setBalance(u.getBalance() + bonusPoints);
                userRepository.save(u);
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
