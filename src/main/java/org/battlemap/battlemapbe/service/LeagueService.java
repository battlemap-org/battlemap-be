package org.battlemap.battlemapbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.model.Leagues;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.battlemap.battlemapbe.repository.LeaguesRepository;
import org.battlemap.battlemapbe.repository.UserLeaguesRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeagueService {

    private final UserLeaguesRepository userLeaguesRepository;
    private final UserRepository userRepository;
    private final LeaguesRepository leaguesRepository;

     // 이번 시즌 리더보드 + 남은 시간 + 내 시즌 포인트 표시
     // - 리그 포인트: UserLeagues.leaguePoint 기준
     // - 사용 가능 포인트(point)는 여기서 직접 변경 X
     // - 리그 종료 + 미정산이면: 시즌 정산(포인트 이월) 실행

    public LeagueResponse getMonthlyLeaderboard(String loginId, String cityName) {

        // 로그인 유저 검증
        Users me = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        // 현재 진행 중인 리그 조회 (기간 내 && settled = false/true 상관없이)
        LocalDateTime now = LocalDateTime.now();
        Leagues currentLeague = leaguesRepository.findCurrentLeague(now)
                .orElseThrow(() -> new IllegalArgumentException("LEAGUE_NOT_FOUND"));

        // 현재 리그 기준 리더보드 조회 (leaguePoint DESC)
        List<UserLeagues> userLeagues = userLeaguesRepository
                .findByLeaguesOrderByLeaguePointDesc(currentLeague);

        List<LeaderboardResponseDto> leaderboard = new ArrayList<>();

        int rank = 1;
        int myRank = 0;
        int mySeasonPoint = 0;
        String myNickname = me.getName();

        for (UserLeagues ul : userLeagues) {
            Users u = ul.getUsers();
            int leaguePoint = ul.getLeaguePoint();

            leaderboard.add(LeaderboardResponseDto.builder()
                    .rank(rank)
                    .nickname(u.getName())
                    .totalPoints(leaguePoint)    // 화면에 보이는 "리그 포인트"
                    .build());

            if (u.getUserId().equals(me.getUserId())) {
                myRank = rank;
                mySeasonPoint = leaguePoint;
            }

            rank++;
        }

        // 남은 시즌 시간 (리그 endDate 기준)
        String remaining = buildRemainingTime(now, currentLeague.getEndDate());

        // 시즌 종료 후 + 아직 정산 안 했으면 → 정산 수행
        if (now.isAfter(currentLeague.getEndDate()) && !currentLeague.isSettled()) {
            applySeasonBonusAndReset(currentLeague, userLeagues);
        }

        return new LeagueResponse(leaderboard, myRank, myNickname, mySeasonPoint, remaining);
    }

     // 시즌 종료 시:
     //  - leaguePoint + 순위 보너스를 Users.point(사용 가능 포인트)에 적립
     //  - UserLeagues.leaguePoint = 0 으로 리셋
     //  - UserLeagues.userRank 저장
     //  - Leagues.settled = true (중복 정산 방지)
    private void applySeasonBonusAndReset(Leagues league, List<UserLeagues> userLeaguesSorted) {

        int rank = 1;

        for (UserLeagues ul : userLeaguesSorted) {
            Users user = ul.getUsers();
            int basePoints = ul.getLeaguePoint(); // 이번 시즌 동안 쌓은 리그 포인트

            // 순위 기반 보너스 비율
            int bonusRate = switch (rank) {
                case 1 -> 50;          // 1위: +50%
                case 2, 3 -> 30;       // 2~3위: +30%
                case 4, 5 -> 10;       // 4~5위: +10%
                default -> 0;
            };

            int bonusPoints = (basePoints * bonusRate) / 100;
            int totalToAdd = basePoints + bonusPoints;

            // 사용 가능 포인트(point)에 적립
            if (totalToAdd > 0) {
                user.setPoint(user.getPoint() + totalToAdd);
                userRepository.save(user);
            }

            // 시즌 기록 저장 + 리그 포인트 초기화
            ul.setUserRank(rank);
            ul.setLeaguePoint(0);
            userLeaguesRepository.save(ul);

            rank++;
        }

        // 이 리그는 정산 완료 처리 → 재실행 방지
        league.setSettled(true);
        leaguesRepository.save(league);
    }

    // 남은 시간 문자열 생성 ("X일 Y시간 Z분")

    private String buildRemainingTime(LocalDateTime now, LocalDateTime end) {
        if (now.isAfter(end)) {
            return "0일 0시간 0분";
        }
        Duration duration = Duration.between(now, end);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        return String.format("%d일 %d시간 %d분", days, hours, minutes);
    }

    // 응답 DTO (리더보드 + 내 순위/닉네임 + 남은 시간)

    public record LeagueResponse(
            List<LeaderboardResponseDto> leaderboard,
            int myRank,
            String myNickname,
            int mySeasonPoint,
            String remainingTime
    ) {}
}
