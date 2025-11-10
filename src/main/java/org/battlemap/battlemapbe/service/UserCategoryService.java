package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserCategoryRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

    /**
     * ✅ 유저별 가장 많이 퀘스트 완료한 카테고리 조회
     */
    @Transactional(readOnly = true)
    public String findMostActiveCategory(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        return userCategoryRepository.findTopCategoryByUser(user.getUserId())
                .map(result -> (String) result[0]) // category_group_name
                .orElse("데이터 없음");
    }
}
