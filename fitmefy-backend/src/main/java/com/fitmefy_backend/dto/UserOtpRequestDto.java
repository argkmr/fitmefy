package com.fitmefy_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class UserOtpRequestDto {
    private String name;
    private String email;
    private UUID id;
    private Integer otp;
}
