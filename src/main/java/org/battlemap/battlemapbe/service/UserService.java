package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.battlemap.battlemapbe.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ 회원가입
    public void registerUser(Users user) {
        // 아이디 중복 검사
        if (userRepository.findByLoginId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        // 🔍 비밀번호 null 체크 추가
        if (user.getPw() == null || user.getPw().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        // 🔍 형식 검사
        if (!user.getPw().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$")) {
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
        }

        // 비밀번호 암호화 후 저장
        user.setPw(passwordEncoder.encode(user.getPw()));
        userRepository.save(user);
    }

    // ✅ 로그인
    public String login(String id, String pw) {
        Users user = userRepository.findByLoginId(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 아이디 또는 비밀번호입니다."));

        if (!passwordEncoder.matches(pw, user.getPw())) {
            throw new IllegalArgumentException("잘못된 아이디 또는 비밀번호입니다.");
        }

        return jwtTokenProvider.generateToken(user.getId());
    }
}