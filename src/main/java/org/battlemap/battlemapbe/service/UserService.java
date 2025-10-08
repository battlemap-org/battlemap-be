package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.model.User;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) {
        // 아이디 중복 검사
        if (userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }

        // 이메일 중복 검사
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        // 비밀번호 형식 검사
        if (!user.getPw().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$")) {
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
        }

        // 저장
        userRepository.save(user);
    }
}