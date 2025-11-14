package org.battlemap.battlemapbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.model.Leagues;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;
import org.battlemap.battlemapbe.repository.LeaguesRepository;
import org.battlemap.battlemapbe.repository.UserLeagueRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeagueService {

    private final UserLeagueRepository userLeagueRepository;
    private final UserRepository userRepository;
    private final LeaguesRepository leaguesRepository;

    // 현재 진행 중인 리그 조회
    // 리그가 없으면 자동으로 새 시즌을 생성하도록 수정
    public Leagues getCurrentLeagueOrThrow() {
        LocalDateTime now = LocalDateTime.now();

        // 리그가 없으면 createNextMonthlyLeague를 호출하여 생성
        return leaguesRepository.findCurrentLeague(now)
                .orElseGet(() -> {
                    // QuestService는 cityName 정보를 직접 전달받지 않으므로,
                    // createNextMonthlyLeague 메서드가 동작할 수 있도록 "부천시"를 기본값으로 사용
                    System.out.println("League not found. Creating a new monthly league for Bucheon-si.");
                    return createNextMonthlyLeague(now, "부천시");
                });
    }


    // 이번 시즌 리더보드 조회
    public LeagueResponse getMonthlyLeaderboard(String loginId, String cityName) {

        // 로그인 유저 확인
        Users me = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        LocalDateTime now = LocalDateTime.now();

        // 진행 중인 리그 없으면 → 새 시즌 생성해서 사용
        Leagues currentLeague = leaguesRepository.findCurrentLeague(now)
                .orElseGet(() -> createNextMonthlyLeague(now, cityName));

        // 현재 리그 기준 유저 포인트 조회
        List<UserLeagues> userLeagues =
                userLeagueRepository.findByLeaguesOrderByLeaguePointDesc(currentLeague);

        List<LeaderboardResponseDto> leaderboard = new ArrayList<>();
        int rank = 1;              // 리더보드 표시 순위 (0점 제외)
        int myRank = 0;            // 내가 리더보드에 들었으면 순위, 아니면 0
        int mySeasonPoint = 0;     // 내 시즌 포인트 (없으면 0)
        String myNickname = me.getName();
        String myUserColorCode = me.getUserColorCode(); // 내 색상 코드 조회

        for (UserLeagues ul : userLeagues) {
            Users u = ul.getUsers();
            int leaguePoint = ul.getLeaguePoint();

            // 0점은 리더보드에 안 보이게
            if (leaguePoint <= 0) {
                // mySeasonPoint 는 0으로 유지
                if (u.getUserId().equals(me.getUserId())) {
                    mySeasonPoint = 0;
                }
                continue;
            }

            // 리더보드에 노출
            leaderboard.add(LeaderboardResponseDto.builder()
                    .rank(rank)
                    .nickname(u.getName())
                    .totalPoints(leaguePoint)
                    .userColorCode(u.getUserColorCode()) // 사용자 색상 코드 포함
                    .build());

            // 내 순위 / 포인트 세팅
            if (u.getUserId().equals(me.getUserId())) {
                myRank = rank;
                mySeasonPoint = leaguePoint;
            }

            rank++;
        }

        String remainingTime = buildRemainingTime(now, currentLeague.getEndDate());

        return new LeagueResponse(leaderboard, myRank, myNickname, mySeasonPoint, myUserColorCode, remainingTime);
    }

    // endDate 지난 시즌들 정산 (스케줄러 / 수동에서 호출)
    public void settleExpiredLeagues() {
        LocalDateTime now = LocalDateTime.now();
        var expiredLeagues = leaguesRepository.findExpiredUnsettledLeagues(now);

        for (Leagues league : expiredLeagues) {
            List<UserLeagues> userLeagues =
                    userLeagueRepository.findByLeaguesOrderByLeaguePointDesc(league);
            applySeasonBonusAndReset(league, userLeagues);
        }
    }

    // 시즌 정산 로직 (리그 포인트 이월 + 보너스)
    private void applySeasonBonusAndReset(Leagues league, List<UserLeagues> userLeaguesSorted) {
        int rank = 1;

        for (UserLeagues ul : userLeaguesSorted) {
            Users user = ul.getUsers();
            int basePoints = ul.getLeaguePoint();

            int bonusRate = switch (rank) {
                case 1 -> 50;     // 1위 +50%
                case 2, 3 -> 30;  // 2~3위 +30%
                case 4, 5 -> 10;  // 4~5위 +10%
                default -> 0;
            };

            int bonusPoints = (basePoints * bonusRate) / 100;
            int totalToAdd = basePoints + bonusPoints;

            if (totalToAdd > 0) {
                user.setPoint(user.getPoint() + totalToAdd);
                userRepository.save(user);
            }

            ul.setUserRank(rank);
            ul.setLeaguePoint(0);
            userLeagueRepository.save(ul);

            rank++;
        }

        league.setSettled(true);
        leaguesRepository.save(league);
    }

    // 진행 중인 리그가 없으면 "다음 시즌" 자동 생성
    // 시즌: 매달 1일 00:00 ~ 말일 23:59:59
    private Leagues createNextMonthlyLeague(LocalDateTime now, String cityName) {
        YearMonth ym = YearMonth.from(now);

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        // 이미 이 달도 끝난 상태면 → 다음 달 시즌으로
        if (now.isAfter(end)) {
            ym = ym.plusMonths(1);
            start = ym.atDay(1).atStartOfDay();
            end = ym.atEndOfMonth().atTime(23, 59, 59);
        }

        String leagueName = cityName + " " + ym.getMonthValue() + "월 시즌";

        Leagues newLeague = Leagues.builder()
                .leagueName(leagueName)
                .startDate(start)
                .endDate(end)
                .settled(false)
                .build();

        return leaguesRepository.save(newLeague);
    }

    // 남은 시즌 시간 계산
    private String buildRemainingTime(LocalDateTime now, LocalDateTime end) {
        if (now.isAfter(end)) {
            return "0일 0시간 0분";
        }
        Duration d = Duration.between(now, end);
        return String.format("%d일 %d시간 %d분", d.toDays(), d.toHours() % 24, d.toMinutes() % 60);
    }

    // 응답 DTO
    public record LeagueResponse(
            List<LeaderboardResponseDto> leaderboard,
            int myRank,
            String myNickname,
            int mySeasonPoint,
            String myUserColorCode, // 사용자 본인 색상 코드 필드
            String remainingTime
    ) {}
}