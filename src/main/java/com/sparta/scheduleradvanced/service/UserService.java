package com.sparta.scheduleradvanced.service;

import com.sparta.scheduleradvanced.dto.LoginRequestDto;
import com.sparta.scheduleradvanced.dto.SignupRequestDto;
import com.sparta.scheduleradvanced.entity.User;
import com.sparta.scheduleradvanced.entity.RefreshToken;
import com.sparta.scheduleradvanced.entity.UserRoleEnum;
import com.sparta.scheduleradvanced.jwt.JwtUtil;
import com.sparta.scheduleradvanced.repository.RefreshTokenRepository;
import com.sparta.scheduleradvanced.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원가입
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 username 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // DB에 사용자 등록
        User user = new User(username, password, requestDto.getNickname(), role);
        userRepository.save(user);
    }

    // 로그인
    public void login(LoginRequestDto requestDto, HttpServletResponse response) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        // username 일치  확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        // password 일치 확인
        if (!password.equals(user.getPassword())) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        // JWT 생성(발급)
        String AccessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole()); // Access Token
        String RefreshToken = jwtUtil.createRefreshToken(user.getUsername()); // Refresh Token
        // Refresh Token 저장
        refreshTokenRepository.save(new RefreshToken(RefreshToken, user));
        // Header에 Access Token, Refresh Token 추가
        response.addHeader(JwtUtil.ACCESS_TOKEN_HEADER, jwtUtil.addPrefix(AccessToken));
        response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, jwtUtil.addPrefix(RefreshToken));
    }
}