package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.dto.SignupRequestDto;
import com.sparta.scheduleradvanced.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /*회원가입*/
    @PostMapping("/user/signup")
    @ResponseBody
    public String signup(@Valid @RequestBody SignupRequestDto requestDto, HttpServletResponse response) {
        userService.signup(requestDto); // 저장
        response.setStatus(HttpStatus.CREATED.value());
        return "회원가입 완료";
    }
}