package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeagueRequestDto;
import org.battlemap.battlemapbe.dto.league.LeagueResponseDto;
import org.battlemap.battlemapbe.dto.league.LeagueRankingRowDto;
import org.battlemap.battlemapbe.dto.league.LeagueLogItemDto;
import org.battlemap.battlemapbe.dto.league.LeagueLogResponseDto;
import org.battlemap.battlemapbe.model.mapping.LeagueStatus;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueService {

    /** 리그 메타(목) */
    private final List<LeagueResponseDto> mockLeagueList = new ArrayList<>();

    /** 리그별 영토(목) */
    private final Map<Long, List<String>> leagueTerritories = new ConcurrentHashMap<>();

    /** 리그별 팀 점수(목) : leagueId -> (teamName -> score) */
    private final Map<Long, Map<String, Integer>> leagueScores = new ConcurrentHashMap<>();

    /** 리그별 전투 로그(목, 최신 순 정렬 유지) : leagueId -> List(logs) */
    private final Map<Long, List<LeagueLogItemDto>> leagueLogs = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        // 리그 2개 목 데이터
        mockLeagueList.add(LeagueResponseDto.of(
                1L, "경기-역곡동 주간 리그", "경기", "역곡동",
                LeagueStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(6)
        ));
        mockLeagueList.add(LeagueResponseDto.of(
                2L, "경기-상동 예선", "경기", "상동",
                LeagueStatus.SCHEDULED,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10)
        ));

        // 영토(목)
        leagueTerritories.put(1L, List.of("역곡1구역", "역곡2구역", "역곡역주변"));
        leagueTerritories.put(2L, List.of("상동호수공원", "상동시장"));

        // 팀 점수(목)
        leagueScores.putIfAbsent(1L, new ConcurrentHashMap<>());
        leagueScores.get(1L).put("고은우", 120);
        leagueScores.get(1L).put("김민지", 95);
        leagueScores.get(1L).put("장주은", 135);

        leagueScores.putIfAbsent(2L, new ConcurrentHashMap<>());
        leagueScores.get(2L).put("상동A", 30);
        leagueScores.get(2L).put("상동B", 45);

        // 로그(목) — 최신이 앞(내림차순)으로 들어가게 관리
        leagueLogs.putIfAbsent(1L, new ArrayList<>());
        appendLog(1L, LeagueLogItemDto.of(nowMs(), "장주은", "CAPTURE", "역곡역주변 점령(+20)"));
        appendLog(1L, LeagueLogItemDto.of(nowMs() - 60_000, "고은우", "ASSIST", "역곡1구역 수비 지원(+5)"));
        appendLog(1L, LeagueLogItemDto.of(nowMs() - 120_000, "김민지", "CAPTURE_FAIL", "역곡2구역 점령 실패(0)"));

        leagueLogs.putIfAbsent(2L, new ArrayList<>());
        appendLog(2L, LeagueLogItemDto.of(nowMs(), "상동A", "CAPTURE", "상동시장 점령(+10)"));
    }

    /* ---------- 기본 CRUD(목) ---------- */

    public List<LeagueResponseDto> getAllLeagues(LeagueStatus status) {
        if (status == null) return List.copyOf(mockLeagueList);
        return mockLeagueList.stream()
                .filter(dto -> dto.getStatus() == status)
                .toList();
    }

    public void createLeague(LeagueRequestDto requestDto) {
        long nextId = mockLeagueList.stream()
                .mapToLong(l -> l.getId() == null ? 0L : l.getId())
                .max().orElse(0L) + 1;

        mockLeagueList.add(LeagueResponseDto.of(
                nextId,
                requestDto.getLeagueName(),
                requestDto.getRegion(),
                requestDto.getDong(),
                LeagueStatus.SCHEDULED,
                requestDto.getStartAt(),
                requestDto.getEndAt()
        ));

        leagueTerritories.putIfAbsent(nextId, new ArrayList<>());
        leagueScores.putIfAbsent(nextId, new ConcurrentHashMap<>());
        leagueLogs.putIfAbsent(nextId, new ArrayList<>());
    }

    public LeagueResponseDto getLeague(Long leagueId) {
        return mockLeagueList.stream()
                .filter(l -> Objects.equals(l.getId(), leagueId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("리그를 찾을 수 없다: id=" + leagueId));
    }

    public LeagueResponseDto setActive(Long leagueId, boolean enable) {
        LeagueResponseDto target = getLeague(leagueId);
        LeagueResponseDto updated = LeagueResponseDto.of(
                target.getId(),
                target.getLeagueName(),
                target.getRegion(),
                target.getDong(),
                enable ? LeagueStatus.ACTIVE : LeagueStatus.ENDED,
                target.getStartAt(),
                target.getEndAt()
        );

        for (int i = 0; i < mockLeagueList.size(); i++) {
            if (Objects.equals(mockLeagueList.get(i).getId(), leagueId)) {
                mockLeagueList.set(i, updated);
                break;
            }
        }
        return updated;
    }

    /* ---------- 영토 ---------- */

    public List<String> getTerritories(Long leagueId) {
        if (!existsLeague(leagueId)) return List.of(); // 200 + 빈 배열(원하면 404 예외로 바꿔도 됨)
        return leagueTerritories.getOrDefault(leagueId, List.of());
    }

    public void setTerritories(Long leagueId, List<String> territories) {
        leagueTerritories.put(leagueId, new ArrayList<>(territories));
    }

    /* ---------- 랭킹(목) ---------- */

    /** 상위 N개 팀 랭킹 반환 (점수 내림차순, 동점이면 팀명 알파벳순) */
    public List<LeagueRankingRowDto> getRank(Long leagueId, int top) {
        if (!existsLeague(leagueId)) return List.of();
        Map<String, Integer> scores = leagueScores.getOrDefault(leagueId, Map.of());

        List<Map.Entry<String, Integer>> sorted = scores.entrySet().stream()
                .sorted(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue).reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(top <= 0 ? 10 : top)
                .collect(Collectors.toList());

        List<LeagueRankingRowDto> rows = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<String, Integer> e : sorted) {
            rows.add(LeagueRankingRowDto.of(rank++, e.getKey(), e.getValue()));
        }
        return rows;
    }

    /* ---------- 로그(목, 커서 페이지네이션) ---------- */

    /** 커서(밀리초)보다 과거 로그를 최신순으로 최대 limit개 반환 */
    public LeagueLogResponseDto getLogs(Long leagueId, int limit, Long cursorMillis) {
        if (!existsLeague(leagueId)) {
            return LeagueLogResponseDto.of(List.of(), null);
        }
        List<LeagueLogItemDto> all = leagueLogs.getOrDefault(leagueId, List.of());
        long cursor = (cursorMillis == null ? Long.MAX_VALUE : cursorMillis);

        // 최신순(all 자체가 최신순 유지)에서 cursor 이전 것만 필터
        List<LeagueLogItemDto> filtered = all.stream()
                .filter(l -> l.getTsMillis() < cursor)
                .limit(limit <= 0 ? 50 : limit)
                .collect(Collectors.toList());

        Long nextCursor = filtered.isEmpty() ? null
                : filtered.get(filtered.size() - 1).getTsMillis();

        return LeagueLogResponseDto.of(filtered, nextCursor);
    }

    /** 로그 1건 추가(목) — 최신이 앞에 오도록 관리 */
    public void appendLog(Long leagueId, LeagueLogItemDto item) {
        leagueLogs.putIfAbsent(leagueId, new ArrayList<>());
        List<LeagueLogItemDto> list = leagueLogs.get(leagueId);
        // 최신 우선으로 앞에 삽입
        list.add(0, item);
    }

    /* ---------- 내부 유틸 ---------- */

    private boolean existsLeague(Long leagueId) {
        return mockLeagueList.stream().anyMatch(l -> Objects.equals(l.getId(), leagueId));
    }

    private long nowMs() {
        return Instant.now().toEpochMilli();
    }


}
