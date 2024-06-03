package com.sparta.scheduleradvanced.jwt;

import com.sparta.scheduleradvanced.config.JwtConfig;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.entity.UserRoleEnum;
import com.sparta.scheduleradvanced.exception.TokenException;
import com.sparta.scheduleradvanced.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // JWT - Access Token 생성
    public String createAccessToken(String username, UserRoleEnum role) {
        Date date = new Date();
        // AccessToken
        return Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .claim(JwtConfig.AUTHORIZATION_KEY, role) // 사용자 권한
                .setExpiration(new Date(date.getTime() + JwtConfig.ACCESS_TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(JwtConfig.key, JwtConfig.SIGNATURE_ALGORITHM) // 암호화 알고리즘
                .compact();
    }

    // JWT - Refresh Token 생성
    public String createRefreshToken(String username) {
        Date date = new Date();
        // Refresh Token
        return Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setExpiration(new Date(date.getTime() + JwtConfig.REFRESH_TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(JwtConfig.key)
                .compact();
    }

    // Header 에서 JWT - Access Token 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConfig.ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfig.BEARER_PREFIX)) {
            return bearerToken.replace(JwtConfig.BEARER_PREFIX, ""); //BEARER_PREFIX 삭제
        }
        return null;
    }

    // Header 에서 JWT - Refresh Token 가져오기
    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConfig.REFRESH_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfig.BEARER_PREFIX)) {
            return bearerToken.replace(JwtConfig.BEARER_PREFIX, ""); //BEARER_PREFIX 삭제
        }
        return null;
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    // JWT 사용자 정보 가지고 오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token).getBody();
    }

    // Header에서 가져온 JWT에서 User 찾아서 반환하기 (토큰에 부합한 User일때만 기능 사용을 위함)
    public User getUserFromHeader(HttpServletRequest request) {
        // Header에서 JWT 가지고 오기
        String token = this.getAccessTokenFromHeader(request);
        if (token == null) {
            token = this.getRefreshTokenFromHeader(request);
        }
        // JWT 검증
        if (!this.validateToken(token)) {
            throw new TokenException("토큰이 유효하지 않습니다.");
        }
        // 토큰 해당하는 User 찾기
        String username = this.getUserInfoFromToken(token).getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new TokenException("토큰이 유효하지 않습니다.")
        );
        return user;
    }

    /*Header에 넣을 Token만들기(PREFIX) 붙이기*/
    public String addPrefix(String token) {
        return JwtConfig.BEARER_PREFIX + token;
    }
}