package com.fitmefy_backend.dto;

import com.fitmefy_backend.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationResponseDto {
    private UserType type;
    private UUID id;
    private String email;
    private String name;
    private String role;
}
