package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongPointRowDto;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserOccupyPointsRepository;
import org.battlemap.battlemapbe.repository.UserQuestsRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 비즈니스 로직 층
 * - MySQL(JPA)에서 동별 포인트/완료수 집계 쿼리를 호출하고
 * - 화면에서 요구하는 형태의 DTO로 병합한다.
 */
@Service
@RequiredArgsConstructor
public class PointQueryService {

    private final UserRepository userRepository;                // ✅ loginId → userId(PK) 매핑용
    private final UserOccupyPointsRepository occupyRepo;        // ✅ 동별 포인트 합계
    private final UserQuestsRepository questsRepo;              // ✅ 동별 완료 퀘스트 수

    /**
     * 로그인 아이디(loginId) 기준으로 "나의" 동별 포인트/완료수 목록 반환
     * 1) loginId로 Users 조회 → PK(userId) 얻기
     * 2) userId로 동별 포인트 합계 조회
     * 3) userId로 동별 완료 퀘스트 수 조회
     * 4) dongName 기준으로 병합하여 DTO 구성
     */
    @Transactional(readOnly = true)
    public List<DongPointRowDto> getMyDongPoints(String loginId) {

        // 1) loginId → Users → userId(PK)
        Users user = userRepository.findById(loginId)   // ⚠️ 아래 UserRepository에 메서드 추가 필요
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + loginId));
        Long userId = user.getUserId();

        // 2) 동별 포인트 합계
        var pointRows = occupyRepo.findMyDongPoints(userId);

        // 3) 동별 완료 퀘스트 수 → Map(dongName → count)
        Map<String, Long> completedByDong = new HashMap<>();
        questsRepo.countMyCompletedByDong(userId).forEach(row ->
                completedByDong.put(row.getDongName(), row.getCompletedCount())
        );

        // 4) 병합하여 DTO 리스트 생성
        return pointRows.stream()
                .map(p -> new DongPointRowDto(
                        p.getCityName(),
                        p.getDongName(),
                        p.getMyPoints() == null ? 0 : p.getMyPoints(),
                        completedByDong.getOrDefault(p.getDongName(), 0L)
                ))
                .toList();
    }
}
