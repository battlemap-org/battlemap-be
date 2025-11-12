package org.battlemap.battlemapbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.dto.login.LoginResponse;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.battlemap.battlemapbe.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors; // List 작업용 import 추가

import java.util.Map;
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 색상 풀(Color Pool) 정의
    private static final List<String> COLOR_POOL = List.of(
// 기본 원색 및 고채도 색상 (Vivid & Primary)
            "#FF0000", // 빨강
            "#008000", // 초록
            "#0000FF", // 파랑
            "#FFD700", // 금색
            "#FF8C00", // 진한 주황
            "#800080", // 보라
            "#00FFFF", // 하늘
            "#FF00FF", // 마젠타

            // 추가 고채도 색상 (Highly Saturated)
            "#4169E1", // 로열 블루
            "#B8860B", // 다크 골든로드
            "#228B22", // 포레스트 그린
            "#8B0000", // 다크 레드
            "#FFA07A", // 라이트 살몬
            "#00BFFF", // 딥 스카이 블루
            "#FF1493", // 딥 핑크
            "#7CFC00", // 라임 그린

            // 명도가 다른 중간 색상 (Distinguishable Intermediate)
            "#808000", // 올리브
            "#4682B4", // 스틸 블루
            "#9932CC", // 다크 오키드
            "#20B2AA", // 라이트 시 그린
            "#D2691E", // 초콜릿
            "#6A5ACD", // 슬레이트 블루
            "#FF4500", // 오렌지 레드
            "#CD5C5C", // 인디언 레드
            "#32CD32"  // 라임
    );

    // 회원가입
    public void registerUser(Users user) {
        // 아이디 중복 검사
        if (userRepository.findByLoginId(user.getId()).isPresent()) {
            throw new CustomException("USER_409", "중복된 아이디입니다.", HttpStatus.CONFLICT);
        }

        // 이메일 중복 검사
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new CustomException("USER_409", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT);
        }

        // 비밀번호 검사
        if (user.getPw() == null || user.getPw().isEmpty()) {
            throw new CustomException("USER_400", "비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        if (!user.getPw().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$")) {
            throw new CustomException("USER_400", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        // 기본 포인트 설정 (없으면 2000으로)
        if (user.getPoint() == 0) {
            user.setPoint(3000);
        }

        // 회원가입 전 사용자에게 색상 할당
        assignInitialColor(user);

        // 비밀번호 암호화 후 저장
        user.setPw(passwordEncoder.encode(user.getPw()));
        userRepository.save(user);
    }

    // 색상 할당 로직 구현
    private void assignInitialColor(Users user) {
        // 현재 DB에 사용 중인 모든 색상 코드를 조회
        // (UserRepository에 정의한 findAllColors() 사용)
        List<String> usedColors = userRepository.findAllColors();

        // 사용되지 않은 색상을 찾음
        String newColor = COLOR_POOL.stream()
                .filter(color -> !usedColors.contains(color))
                .findFirst()
                // 만약 모든 색상이 사용 중이면, Pool에서 무작위로 하나를 선택하여 할당
                .orElseGet(() -> {
                    Random random = new Random();
                    return COLOR_POOL.get(random.nextInt(COLOR_POOL.size()));
                });

        user.setUserColorCode(newColor);
    }

    // 로그인
    public LoginResponse login(String id, String pw) {
        Users user = userRepository.findByLoginId(id)
                .orElseThrow(() -> new CustomException("USER_404", "잘못된 아이디 또는 비밀번호입니다.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(pw, user.getPw())) {
            throw new CustomException("USER_401", "잘못된 아이디 또는 비밀번호입니다.", HttpStatus.UNAUTHORIZED);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getId());
        user.setToken(token);
        userRepository.save(user);

        // DTO 생성
        return LoginResponse.builder()
                .userId(user.getUserId())
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .userColorCode(user.getUserColorCode()) // 로그인 응답에 색상 코드 포함
                .build();
    }

    // 토큰 기반 사용자 이름 조회 (Map 반환)
    public Map<String, Object> getUserNameByToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new CustomException("TOKEN_400", "토큰 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        String accessToken = token.substring(7);
        String loginId;

        try {
            loginId = jwtTokenProvider.validateAndGetUserId(accessToken);
        } catch (RuntimeException e) {
            throw new CustomException("TOKEN_401", "유효하지 않거나 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));

        return Map.of("userName", user.getName());
    }
    // 보유 포인트 조회
    public int getUserPoints(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));
        return user.getPoint();
    }

    // 로그아웃
    public void logout(String token) {
        // Bearer 제거 및 토큰 유효성 검증
        if (token == null || !token.startsWith("Bearer ")) {
            throw new CustomException("TOKEN_400", "토큰 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        String accessToken = token.substring(7);
        String userId;

        // 토큰으로부터 userId 추출
        try {
            userId = jwtTokenProvider.validateAndGetUserId(accessToken);
        } catch (RuntimeException e) {
            throw new CustomException("TOKEN_401", "유효하지 않거나 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        Users user = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new CustomException("USER_404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (user.getToken() == null || !user.getToken().equals(accessToken)) {
            throw new CustomException("LOGOUT_409", "이미 무효화된 토큰입니다.", HttpStatus.CONFLICT);
        }

        // 로그아웃 처리
        user.setToken(null);
        userRepository.save(user);
    }
}
