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

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        // 비밀번호 암호화 후 저장
        user.setPw(passwordEncoder.encode(user.getPw()));
        userRepository.save(user);
    }

    // 로그인
    public LoginResponse login(String id, String pw) {
        Users user = userRepository.findByLoginId(id)
                .orElseThrow(() -> new CustomException("USER_404", "잘못된 아이디 또는 비밀번호입니다.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(pw, user.getPw())) {
            throw new CustomException("USER_401", "잘못된 아이디 또는 비밀번호입니다.", HttpStatus.UNAUTHORIZED);
        }

        // JWT 토큰 생성 및 저장
        String token = jwtTokenProvider.generateToken(user.getId());
        user.setToken(token);
        userRepository.save(user);

        // DTO 객체로 변환하여 반환
        return LoginResponse.builder()
                .userId(user.getUserId())
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();
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
