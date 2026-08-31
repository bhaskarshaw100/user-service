package com.bhaskar.userservice.controllers;

import com.bhaskar.userservice.dtos.LoginRequestDto;
import com.bhaskar.userservice.dtos.LoginResponseDto;
import com.bhaskar.userservice.dtos.ResponseStatus;
import com.bhaskar.userservice.dtos.SignUpRequestDto;
import com.bhaskar.userservice.dtos.SignUpResponseDto;
import com.bhaskar.userservice.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> singUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        try {
            boolean isSignedUp = authService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
            if  (!isSignedUp) {
                signUpResponseDto.setStatus(ResponseStatus.FAILURE);
                return new ResponseEntity<>(signUpResponseDto, HttpStatus.CONFLICT);
            }
            signUpResponseDto.setStatus(ResponseStatus.SUCCESS);
            return new ResponseEntity<>(signUpResponseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            signUpResponseDto.setStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(signUpResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("Login request received for email: " + loginRequestDto.getEmail());
        String token = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setStatus(ResponseStatus.SUCCESS);
        HttpHeaders headers = new HttpHeaders();
        if (token == null) {
            loginResponseDto.setStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(loginResponseDto, headers, HttpStatus.UNAUTHORIZED);
        }
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return new ResponseEntity<>(loginResponseDto, headers, HttpStatus.OK);
    }
}
