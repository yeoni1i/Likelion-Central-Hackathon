package com.likelion.hackatonbe.global.jwt;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
public class CustomUserDetails extends User {

    private final Long userId;

    public CustomUserDetails(Long userId, String username, String password) {
        super(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        this.userId = userId;
    }
}