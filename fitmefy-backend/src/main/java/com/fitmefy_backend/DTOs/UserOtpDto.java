package com.fitmefy_backend.DTOs;

import java.util.UUID;

public class UserOtpDto {
    private String email; // optional
    private UUID id;
    private Integer otp; // optional

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }
}
