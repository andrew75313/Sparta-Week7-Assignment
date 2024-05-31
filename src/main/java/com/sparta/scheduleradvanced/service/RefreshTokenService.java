package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtUtil jwtUtil;

    // Access Token 발급
    public String createToken(HttpServletRequest request, HttpServletResponse response) {
        User user = jwtUtil.getUserFromHeader(request); // 해당 토큰에 맞는 사용자 정보 찾기
        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());
        response.addHeader(JwtUtil.ACCESS_TOKEN_HEADER, jwtUtil.addPrefix(accessToken));

        return "토큰 발급 완료";
    }
}
