package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.league.dto.*;
import org.battlemap.battlemapbe.league.model.LeagueStatus;
import org.battlemap.battlemapbe.league.service.LeagueService;
import org.battlemap.battlemapbe.league.service.LeagueTerritoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leagues")
public class LeagueController {

    private final LeagueService leagueService;
    private final LeagueTerritoryService leagueTerritoryService; // ✅ 추가

    /** (1) 리그 목록 조회 (status 필터) */
    @GetMapping
    public ResponseEntity<List<LeagueResponseDto>> getLeagues(
            @RequestParam(required = false) LeagueStatus status
    ) {
        return ResponseEntity.ok(leagueService.getAllLeagues(status));
    }

    /** (2) 리그 등록 (초기화) */
    @PostMapping
    public ResponseEntity<String> createLeague(@RequestBody LeagueRequestDto requestDto) {
        leagueService.createLeague(requestDto);
        return ResponseEntity.ok("리그가 성공적으로 등록되었습니다.");
    }

    /** (3) 리그 상세 조회 */
    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueResponseDto> getLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(leagueService.getLeague(leagueId));
    }

    /** (4) 리그 활성/비활성 전환 */
    @PatchMapping("/{leagueId}/active")
    public ResponseEntity<LeagueResponseDto> toggleActive(
            @PathVariable Long leagueId,
            @RequestParam boolean enable
    ) {
        return ResponseEntity.ok(leagueService.setActive(leagueId, enable));
    }

    /** (5) 리그 대상 지역(territories) 조회 */
    @GetMapping("/{leagueId}/territories")
    public ResponseEntity<List<TerritoryDto>> getTerritories(@PathVariable Long leagueId) {
        // 수정 포인트: DB 연동된 서비스 사용
        return ResponseEntity.ok(leagueTerritoryService.getTerritories(leagueId));
    }

    /** (6) 리그 랭킹 조회 (팀 순위) */
    @GetMapping("/{leagueId}/rank")
    public ResponseEntity<List<LeagueRankingRowDto>> getRank(
            @PathVariable Long leagueId,
            @RequestParam(defaultValue = "10") int top
    ) {
        return ResponseEntity.ok(leagueService.getRank(leagueId, top));
    }

    /** (7) 리그 전투 로그 조회 */
    @GetMapping("/{leagueId}/logs")
    public ResponseEntity<LeagueLogResponseDto> getLogs(
            @PathVariable Long leagueId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long cursor
    ) {
        return ResponseEntity.ok(leagueService.getLogs(leagueId, limit, cursor));
    }
}
