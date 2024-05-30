package com.sparta.scheduleradvanced.jwt;

import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.entity.UserRoleEnum;
import com.sparta.scheduleradvanced.exception.TokenException;
import com.sparta.scheduleradvanced.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // 상수값들 설정
    public static final String ACCESS_TOKEN_HEADER = "Access-Token";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 1주일


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT - Access Token 생성
    public String createAccessToken(String username, UserRoleEnum role) {
        Date date = new Date();
        // AccessToken
        return Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    // JWT - Refresh Token 생성
    public String createRefreshToken(String username) {
        Date date = new Date();
        // Refresh Token
        return Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key)
                .compact();
    }


    // Header 에서 JWT - Access Token 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.replace(BEARER_PREFIX, ""); //BEARER_PREFIX 삭제
        }
        return null;
    }

    // Header 에서 JWT - Refresh Token 가져오기
    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.replace(BEARER_PREFIX, ""); //BEARER_PREFIX 삭제
        }
        return null;
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    // JWT 사용자 정보 가지고 오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // Header에서 가져온 JWT에서 User 찾아서 반환하기
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
        return BEARER_PREFIX + token;
    }
}