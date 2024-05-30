package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public RefreshTokenController(RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    /*Access Token 발급*/
    @PostMapping("/token")
    public String createToken(HttpServletRequest request, HttpServletResponse response) {

        return refreshTokenService.createToken(request, response);
    }
}

