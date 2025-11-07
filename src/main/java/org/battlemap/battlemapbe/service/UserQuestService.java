package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserQuestRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserQuestService {

    private final UserRepository userRepository;
    private final UserQuestRepository userQuestRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getQuestCountByLoginId(String loginId) {
        // ✅ loginId → DB의 id 컬럼과 매칭됨 (UserRepository 유지)
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND: " + loginId));

        long totalCount = userQuestRepository.countByUsers(user);
        return Map.of("totalCount", totalCount);
    }
}
