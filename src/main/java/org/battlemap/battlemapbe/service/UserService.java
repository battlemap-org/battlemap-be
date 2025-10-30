package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.battlemap.battlemapbe.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        // ✅ 이메일 중복 검사
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new CustomException("USER_409", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT);
        }
        // 비밀번호 null 체크 추가
        if (user.getPw() == null || user.getPw().isEmpty()) {
            throw new CustomException("USER_400", "비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        // 형식 검사
        if (!user.getPw().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$")) {
            throw new CustomException("USER_400", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 비밀번호 암호화 후 저장
        user.setPw(passwordEncoder.encode(user.getPw()));
        userRepository.save(user);
    }

    // 로그인
    public String login(String id, String pw) {
        Users user = userRepository.findByLoginId(id)
                .orElseThrow(() -> new CustomException("USER_400", "잘못된 아이디 또는 비밀번호입니다.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(pw, user.getPw())) {
            throw new CustomException("USER_400", "잘못된 아이디 또는 비밀번호입니다.", HttpStatus.NOT_FOUND);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getId());

        // 토큰을 DB에 저장
        user.setToken(token);
        userRepository.save(user);

        // 프론트로 토큰 반환
        return token;
    }

    // 로그아웃
    public void logout(String token) {
        // Bearer 제거
        String accessToken = token.replace("Bearer ", "");

        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new CustomException("TOKEN_401", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        // 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        // 해당 사용자의 토큰 무효화(DB에서 삭제)
        Users user = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new CustomException("USER_404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        user.setToken(null);  // DB에 저장된 토큰 삭제
        userRepository.save(user);
    }
}