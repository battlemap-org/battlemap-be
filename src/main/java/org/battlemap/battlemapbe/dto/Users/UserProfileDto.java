package org.battlemap.battlemapbe.dto.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String name;      // 이름
    private String loginId;   // 로그인 아이디
    private int point;        // 포인트
    private int rank;         // 순위
    private int totalQuest;   // 총 퀘스트 수
    private int totalCapture; // 총 점령 수
    private String favoriteCategory; // 많이 활동한 카테고리
}
