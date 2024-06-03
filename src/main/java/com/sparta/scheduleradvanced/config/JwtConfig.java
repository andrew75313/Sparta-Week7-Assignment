package com.sparta.scheduleradvanced.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
public class JwtConfig {
    public static final String ACCESS_TOKEN_HEADER = "Access-Token";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 1주일

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    public static Key key;

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    @PostConstruct // 초기 설정
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }
}
