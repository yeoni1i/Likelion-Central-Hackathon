package com.likelion.hackatonbe.domain.user.controller;

import com.likelion.hackatonbe.domain.user.dto.*;
import com.likelion.hackatonbe.domain.user.service.UserService;
import com.likelion.hackatonbe.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/signup_account")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/parent_info")
    public ResponseEntity<String> saveParentInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ParentInfoRequest request) {
        userService.saveParentName(userDetails.getUserId(), request.getParentName());
        return ResponseEntity.ok("보호자 이름 등록 완료");
    }

    @PostMapping("/signup_child")
    public ResponseEntity<String> saveChildInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OnboardingRequest request) {
        userService.saveOnboardingInfo(userDetails.getUserId(), request);
        return ResponseEntity.ok("아이 정보 등록 완료");
    }

    @GetMapping("/signup_end")
    public ResponseEntity<String> signupEnd() {
        return ResponseEntity.ok("온보딩/등록 완료");
    }
}
