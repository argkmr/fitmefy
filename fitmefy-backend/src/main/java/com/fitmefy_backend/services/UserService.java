package com.fitmefy_backend.services;

import com.fitmefy_backend.dto.UserOtpRequestDto;
import com.fitmefy_backend.dto.UserRegistrationResponseDto;
import com.fitmefy_backend.entities.Otp;
import com.fitmefy_backend.enums.UserRole;
import com.fitmefy_backend.enums.UserType;
import com.fitmefy_backend.repository.OtpRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.fitmefy_backend.entities.User;
import com.fitmefy_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    // or (Constructor based DI)
    // public UserService(RabbitTemplate rabbitTemplate){
    //    this.rabbitTemplate = rabbitTemplate;
    // }

    @Value("${MAIL_USERNAME}")
    private String username;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public UserRegistrationResponseDto setUserInfoInDB(String name, String email, UserRole role) {
        try{
            Optional<User> userInfo = userRepository.findByEmail(email);
            if (userInfo.isPresent()){
                String existingUserEmail = userInfo.get().getEmail();
                if (existingUserEmail.equals(email)){ // don't do existingUserEmail==email it will check the reference not the value
                    return new UserRegistrationResponseDto().builder()
                            .type(UserType.EXISTING_USER)
                            .id(userInfo.get().getId())
                            .email(userInfo.get().getEmail())
                            .name(userInfo.get().getName())
                            .role(userInfo.get().getRole())
                            .build();
                }
            }
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setRole(role.toString());
            user = userRepository.save(user);

            return new UserRegistrationResponseDto().builder()
                    .type(UserType.NEW_USER)
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .build();

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public int generateUserOtp(){
        Random otp = new Random();
        int min = 100000, max=999999;
        return otp.nextInt(max-min);
    }

    public void saveOtpToDb(String email){
        try{
            Integer otp = generateUserOtp();
            Optional<User> userInfo = userRepository.findByEmail(email);
            UserOtpRequestDto userOtpRequestDto = new UserOtpRequestDto();

            System.out.println("Saving the otp to DB");
            // Store the opt in the db
            Optional<User> ExistingUser = userRepository.findByEmail(email);

            userOtpRequestDto.setEmail(email);
            userOtpRequestDto.setOtp(otp);
            userOtpRequestDto.setId(ExistingUser.get().getId());
            userOtpRequestDto.setName(ExistingUser.get().getName());

            Otp otpInfo = Otp.builder()
                    .user(userInfo.get())
                    .otpCode(otp)
                    .build();
            otpRepository.save(otpInfo);
            System.out.println("Otp saved in db successfully, now sending message packet queue.");

            rabbitTemplate.convertAndSend(exchange, routingKey, userOtpRequestDto);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("error saving the otp in db");
        }
    }

    @RabbitListener(queues = {"${rabbitmq.queue.name}"}) // can't use direct variable, has to be a constant
    public void sendOtpToUserEmail(UserOtpRequestDto userOtpRequestDto){
        try{
            // 2. logic to send otp to the email
            System.out.println("Received the message from queue now processing...");

            Context context = new Context();
            context.setVariable("name",userOtpRequestDto.getName());
            context.setVariable("otp", userOtpRequestDto.getOtp());

            String htmlContent = springTemplateEngine.process("otp-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(userOtpRequestDto.getEmail());
            helper.setSubject("Your OTP Code, Fitmefy");
            helper.setText(htmlContent, true);
            helper.setFrom(username);
            mailSender.send(mimeMessage);

            System.out.println("Otp sent to the user successfully");
        }catch(Exception e){
            e.printStackTrace();
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
