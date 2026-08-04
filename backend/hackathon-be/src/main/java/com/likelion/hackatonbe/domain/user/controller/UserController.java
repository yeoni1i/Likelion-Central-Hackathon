package com.likelion.hackatonbe.domain.user.controller;

import com.likelion.hackatonbe.domain.user.dto.*;
import com.likelion.hackatonbe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginRequest request) {
        Long userId = userService.login(request);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/{userId}/onboarding")
    public ResponseEntity<String> onboarding(
            @PathVariable Long userId,
            @RequestBody OnboardingRequest request) {
        userService.saveOnboardingInfo(userId, request);
        return ResponseEntity.ok("온보딩 등록 완료");
    }
}
