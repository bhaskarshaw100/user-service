package com.bhaskar.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String message;
    private ResponseStatus status;
}
