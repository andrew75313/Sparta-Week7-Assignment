package com.sparta.scheduleradvanced.controller;

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

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    /*Access Token 발급*/
    @PostMapping("/token")
    public String createToken(HttpServletRequest request, HttpServletResponse response) {
        return refreshTokenService.createToken(request, response);
    }
}

