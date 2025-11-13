package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserQuestsRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserQuestService {

    private final UserRepository userRepository;
    private final UserQuestsRepository userQuestsRepository;

    // 전체 퀘스트 수 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getQuestCountByLoginId(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND: " + loginId));

        long totalCount = userQuestsRepository.countByUsers(user);
        return Map.of("totalCount", totalCount);
    }

    // 완료된 퀘스트 수 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getCompletedQuestCountByLoginId(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND: " + loginId));

        long totalComplete = userQuestsRepository.countByUsersAndIsCompletedTrue(user);
        return Map.of("totalComplete", totalComplete);
    }
}