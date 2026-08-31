package com.bhaskar.userservice.controllers;

import com.bhaskar.userservice.dtos.LoginRequestDto;
import com.bhaskar.userservice.dtos.LoginResponseDto;
import com.bhaskar.userservice.dtos.ResponseStatus;
import com.bhaskar.userservice.dtos.SignUpRequestDto;
import com.bhaskar.userservice.dtos.SignUpResponseDto;
import com.bhaskar.userservice.exceptions.UserNotFoundException;
import com.bhaskar.userservice.exceptions.WrongPasswordException;
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
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        try {
            log.info("Login request received for email: " + loginRequestDto.getEmail());
            String token = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            loginResponseDto.setStatus(ResponseStatus.SUCCESS);
            loginResponseDto.setMessage("Login successful");
            HttpHeaders headers = new HttpHeaders();
            if (token == null) {
                loginResponseDto.setMessage("Login unsuccessful");
                loginResponseDto.setStatus(ResponseStatus.FAILURE);
                return new ResponseEntity<>(loginResponseDto, headers, HttpStatus.UNAUTHORIZED);
            }
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return new ResponseEntity<>(loginResponseDto, headers, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            loginResponseDto.setMessage("User not found: " + e.getMessage());
            loginResponseDto.setStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(loginResponseDto, HttpStatus.NOT_FOUND);
        } catch (WrongPasswordException e) {
            loginResponseDto.setMessage("Incorrect password, try again");
            loginResponseDto.setStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(loginResponseDto, HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e) {
            log.error("Error occurred while logging in: " + e.getMessage());
            loginResponseDto.setStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(loginResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
