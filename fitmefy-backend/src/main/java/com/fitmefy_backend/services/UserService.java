package com.fitmefy_backend.services;

import com.fitmefy_backend.enums.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.fitmefy_backend.entities.User;
import com.fitmefy_backend.repository.UserRepository;

import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public UUID setUserInfoInDB(String name, String email, UserRole role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role.toString());
        user = userRepository.save(user);
        return user.getId();
    }

    public int generateUserOtp(){
        Random otp = new Random();
        int min = 100000, max=999999;
        return otp.nextInt(max-min);
    }

    public boolean sendOtpToUserEmail(String email, UUID userId, int otp){
        try{
            // 1. Store the opt in the db again the userId
            // 2. logic to send otp to the email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("workspace9801@gmail.com");
            message.setTo(email);
            message.setSubject("Your OTP Code, Fitmefy");
            message.setText("Hi Deepshikha,\n\nTesting Fitmefy\n\nYour OTP is: " + otp + "\n\nPlease do not share it with anyone.\n\nBest Regards,\nAnurag Kumar");
            mailSender.send(message);
            // if email sent successfully and otp stored in db : return true else false;
            return true;

        }catch(Exception e){
            throw new RuntimeException("Error sending mail", e);
        }

    }

    public boolean verifyUserOtp(UUID userId, int otp){
        // get the opt stored in the db for the userId
        // verify it against the otp passed from the arguments
        // if matched return true else false;
        return true;
    }
}
