package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.repository.UserCategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;

    public String findMostActiveCategory(String loginId) {
        // 🔹 실제로 loginId 변수에는 "1", "2" 같은 userId(String)가 들어옵니다.
        Long userId = Long.parseLong(loginId);

        return userCategoryRepository.findTopCategoriesByUserId(userId)
                .stream()
                .findFirst()
                .orElse("데이터 없음");
    }
}
