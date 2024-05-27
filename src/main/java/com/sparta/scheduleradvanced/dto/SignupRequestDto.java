package com.sparta.scheduleradvanced.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    @Size(min=4, max=10, message = "4~10자 사이여야합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "알파벳과 숫자로만 이뤄져야합니다.")
    private String username;

    @NotBlank
    @Size(min=8, max=15, message = "8~15자 사이여야합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "알파벳과 숫자로만 이뤄져야합니다.")
    private String password;

    @NotBlank
    private String nickname;

    private boolean admin = false;

    private String adminToken = "";
}