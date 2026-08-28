package com.bhaskar.userservice.controllers;

import com.bhaskar.userservice.dtos.LoginRequestDto;
import com.bhaskar.userservice.dtos.LoginResponseDto;
import com.bhaskar.userservice.dtos.SignUpRequestDto;
import com.bhaskar.userservice.dtos.SignUpResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/signup")
    public SignUpResponseDto singUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return new SignUpResponseDto();
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return new LoginResponseDto();
    }
}
