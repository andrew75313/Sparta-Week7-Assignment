package com.sparta.scheduleradvanced.controller;

import com.sparta.scheduleradvanced.dto.LoginRequestDto;
import com.sparta.scheduleradvanced.dto.SignupRequestDto;
import com.sparta.scheduleradvanced.dto.UserInfoDto;
import com.sparta.scheduleradvanced.entity.UserRoleEnum;
import com.sparta.scheduleradvanced.security.UserDetailsImpl;
import com.sparta.scheduleradvanced.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /*회원가입*/
    @PostMapping("/user/signup")
    @ResponseBody
    public String signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult, HttpServletResponse response) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            StringBuffer errorMessage = new StringBuffer();
            for (FieldError fieldError : fieldErrors) {
                errorMessage.append((fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage() + "\n"));
            }
            return errorMessage.toString();
        }

        // Success
        userService.signup(requestDto); // 저장
        response.setStatus(HttpStatus.CREATED.value());
        return "회원가입 완료";
    }
}