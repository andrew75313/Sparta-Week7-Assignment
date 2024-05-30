package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.entity.RefreshToken;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.exception.TokenException;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    // Access Token 발급
    public String createToken(HttpServletRequest request, HttpServletResponse response) {
        // Refresh Token DB확인
        String token = jwtUtil.getRefreshTokenFromHeader(request); // Refresh Token 찾기
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                ()-> new TokenException("토큰이 유효하지 않습니다.")
        );
        // Refresh Token 검증
        if (!jwtUtil.validateToken(token)) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenException("로그인을 다시 해주세요.");
        }
        // 사용자 - Refresh Token 확인
        User user = jwtUtil.getUserFromHeader(request); // 해당 토큰에 맞는 사용자 정보 찾기
        String usernameOfToken = jwtUtil.getUserInfoFromToken(refreshToken.getToken()).getSubject();
        if (!usernameOfToken.equals(user.getUsername())) {
            throw new TokenException("토큰이 유효하지 않습니다.");
        }
        // Access Token 발급
        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());
        response.addHeader(JwtUtil.ACCESS_TOKEN_HEADER, jwtUtil.addPrefix(accessToken));

        return "토큰 발급 완료";
    }
}
