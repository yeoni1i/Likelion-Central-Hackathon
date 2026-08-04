package com.likelion.hackatonbe.domain.user.service;

import com.likelion.hackatonbe.domain.user.dto.*;
import com.likelion.hackatonbe.domain.user.entity.*;
import com.likelion.hackatonbe.domain.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    @Transactional
    public Long signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        User user = new User(request.getUsername(), request.getPassword());
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public Long login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user.getId();
    }

    @Transactional
    public void saveOnboardingInfo(Long userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateName(request.getParentName());

        Child child = Child.builder()
                .user(user)
                .name(request.getChildName())
                .birthDate(request.getBirthDate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .skinConditions(request.getSkinConditions())
                .specialNote(request.getSpecialNote())
                .build();

        childRepository.save(child);
    }
}
