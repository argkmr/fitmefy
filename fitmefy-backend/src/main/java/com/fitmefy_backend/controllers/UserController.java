package com.fitmefy_backend.controllers;

import com.fitmefy_backend.DTOs.RegisterUserDto;
import com.fitmefy_backend.DTOs.UserOtpDto;
import com.fitmefy_backend.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitmefy_backend.services.UserService;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> setUserInfo(@RequestBody RegisterUserDto registerUserDto) {
        try{
            if(registerUserDto.getEmail()==null || registerUserDto.getName()==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and Email cannot be empty.");
            }

            Map<String, UUID> response = new HashMap<>();
            UUID id = userService.setUserInfoInDB(registerUserDto.getName(), registerUserDto.getEmail(), UserRole.DEDICATED_USER);
            response.put("userId", id);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendUserOtp(@RequestBody UserOtpDto userOtpDto) {
        if(userOtpDto.getEmail()==null || userOtpDto.getId()==null){
            return ResponseEntity.badRequest().body("Email and Id cannot be empty.");
        }
        int otp = userService.generateUserOtp();
        if(!userService.sendOtpToUserEmail(userOtpDto.getEmail(), userOtpDto.getId(), otp)){
            return ResponseEntity.internalServerError().body("Error sending OTP");
        }
        return ResponseEntity.ok("OTP sent to user successfully");
    }

    @GetMapping("/otp-verify")
    public ResponseEntity<String> verifyUserOtp(@RequestBody UserOtpDto userOtpDto){
        if(userOtpDto.getEmail()==null || userOtpDto.getId()==null || userOtpDto.getOtp()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email, UserId and Otp cannot be empty.");
        }

        if(!userService.verifyUserOtp(userOtpDto.getId(), userOtpDto.getOtp())){
            return ResponseEntity.badRequest().body("Incorrect Opt provided");
        }
        return ResponseEntity.ok("Opt verified");
    }
}
