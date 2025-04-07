package com.fitmefy_backend.controllers;

import com.fitmefy_backend.dto.UserRegistrationRequestDto;
import com.fitmefy_backend.dto.UserOtpRequestDto;
import com.fitmefy_backend.dto.UserRegistrationResponseDto;
import com.fitmefy_backend.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitmefy_backend.services.UserService;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@RequestBody @Valid UserRegistrationRequestDto userRegistrationRequestDto) {
        try{
            if(userRegistrationRequestDto.getEmail()==null || userRegistrationRequestDto.getName()==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and Email cannot be empty.");
            }

            UserRegistrationResponseDto responseDto = userService.setUserInfoInDB(
                                                                    userRegistrationRequestDto.getName(),
                                                                    userRegistrationRequestDto.getEmail(),
                                                                    UserRole.DEDICATED_USER);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendUserOtp(@RequestBody UserOtpRequestDto userOtpRequestDto) {
        if(userOtpRequestDto.getEmail()==null){
            return ResponseEntity.badRequest().body("Email cannot be empty");
        }
        userService.saveOtpToDb(userOtpRequestDto.getEmail());
        return ResponseEntity.ok("OTP sent to user successfully");
    }

    @GetMapping("/otp-verify")
    public ResponseEntity<String> verifyUserOtp(@RequestBody UserOtpRequestDto userOtpRequestDto){
        if(userOtpRequestDto.getEmail()==null || userOtpRequestDto.getId()==null || userOtpRequestDto.getOtp()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email, UserId and Otp cannot be empty.");
        }

        if(!userService.verifyUserOtp(userOtpRequestDto.getId(), userOtpRequestDto.getOtp())){
            return ResponseEntity.badRequest().body("Incorrect Opt provided");
        }
        return ResponseEntity.ok("Opt verified");
    }
}
