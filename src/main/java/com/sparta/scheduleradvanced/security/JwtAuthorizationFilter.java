package com.sparta.scheduleradvanced.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.scheduleradvanced.exception.TokenException;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

// JWT 검증 및 인가
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Jwt 가져오기
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Header에서 JWT 가지고 오기
            String tokenValue = jwtUtil.getAccessTokenFromHeader(req); // Access Token
            if (tokenValue == null) {
                tokenValue = jwtUtil.getRefreshTokenFromHeader(req); // Refresh Token
            }

            if (StringUtils.hasText(tokenValue)) {
                // Failure
                if (!jwtUtil.validateToken(tokenValue)) {
                    throw new TokenException("토큰이 유효하지 않습니다.");
                }

                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

                try {
                    setAuthentication(info.getSubject());
                } catch (Exception e) {
                    throw new TokenException("토큰이 유효하지 않습니다.");
                }
            }

            filterChain.doFilter(req, res);
        } catch (TokenException e) {
            sendMessage(res, "토큰이 유효하지 않습니다.");
        }
    }

    // SecurityContextHolder에 인증 User 세팅
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Authentication 구현체
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