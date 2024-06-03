package com.sparta.scheduleradvanced.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.scheduleradvanced.config.JwtConfig;
import com.sparta.scheduleradvanced.dto.LoginRequestDto;
import com.sparta.scheduleradvanced.entity.RefreshToken;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.entity.UserRoleEnum;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.repository.RefreshTokenRepository;
import com.sparta.scheduleradvanced.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

// 로그인 & JWT 인증
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;

        setFilterProcessesUrl("/api/user/login");
    }


    // 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(req.getInputStream(), LoginRequestDto.class);

            // AuthenticationManager 인증요청
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("잘못퇸 입력입니다.");
        }
    }


    // Success
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authResult) throws IOException {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        // username 일치  확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        // JWT 생성(발급)
        String AccessToken = jwtUtil.createAccessToken(username, role); // Access Token
        String RefreshToken = jwtUtil.createRefreshToken(username); // Refresh Token
        // Refresh Token 저장
        refreshTokenRepository.save(new RefreshToken(RefreshToken, user));
        // Header에 Access Token, Refresh Token 추가
        res.addHeader(JwtConfig.ACCESS_TOKEN_HEADER, jwtUtil.addPrefix(AccessToken));
        res.addHeader(JwtConfig.REFRESH_TOKEN_HEADER, jwtUtil.addPrefix(RefreshToken));
        // 메시지 응답
        sendMessage(res, "로그인 성공");
    }

    // Failure
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) throws IOException {
        sendMessage(res, "로그인 실패");
    }

    /*응답 메서드*/
    private void sendMessage(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpStatus.BAD_REQUEST.value()); // StatusCode 400으로

        // JSON 형태로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        res.setContentType("application/json;charset=UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(Map.of("status", HttpStatus.BAD_REQUEST.value(), "msg", message));

        res.getWriter().write(jsonResponse);
    }

}