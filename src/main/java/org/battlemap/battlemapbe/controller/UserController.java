package org.battlemap.battlemapbe.controller;

import org.battlemap.battlemapbe.model.User;
import org.battlemap.battlemapbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok().body("{\"message\": \"회원가입 성공\"}");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("중복")) {
                return ResponseEntity.status(409).body("{\"error\": \"" + e.getMessage() + "\"}");
            }
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"서버 내부 오류\"}");
        }
    }
}