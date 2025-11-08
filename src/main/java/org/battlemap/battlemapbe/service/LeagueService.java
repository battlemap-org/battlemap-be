package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.repository.UserLeaguesRepository;
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

    // 이번 달 리더보드 + 남은 시간 계산
    public LeagueResponse getMonthlyLeaderboard() {
        List<Object[]> results = userLeaguesRepository.findCurrentMonthLeaderboard();
        List<LeaderboardResponseDto> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Object[] row : results) {
            String nickname = (String) row[0];
            int totalPoints = ((Number) row[1]).intValue();

            leaderboard.add(
                    LeaderboardResponseDto.builder()
                            .rank(rank++)
                            .nickname(nickname)
                            .totalPoints(totalPoints)
                            .build()
            );
        }

        // 🔹 이번 달 남은 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59);
        Duration duration = Duration.between(now, endOfMonth);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        String remaining = String.format("%d일 %d시간 %d분", days, hours, minutes);

        return new LeagueResponse(leaderboard, remaining);
    }

    // 내부 응답용 DTO
    public record LeagueResponse(List<LeaderboardResponseDto> leaderboard, String remainingTime) {}
}
