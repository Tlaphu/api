package com.ra.base_spring_boot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Sử dụng email đã được cấu hình trong MailConfig
    
    @Value("${spring.mail.username:thaihoangiaphu004@gmail.com}")
    private String fromEmail;
 

    /**
     * Gửi email chứa link kích hoạt tài khoản
     * @param toEmail Email người nhận
     * @param recipientName Tên người nhận
     * @param confirmationLink Đường link để người dùng click kích hoạt
     */
    public void sendVerificationEmail(String toEmail, String recipientName, String confirmationLink) {
        String userName = recipientName != null && !recipientName.isEmpty() ? recipientName : "User";
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Activate your account on the Recruitment System");

            String content = "Hi " + userName + ",\n\n"
                           + "Please click the link below to activate your account:\n\n"
                           + confirmationLink + "\n\n"
                           + "If you did not register for this account, please ignore the email.\n\n"
                           + "Best regards,\n"
                           + "The Recruitment Team.";

            message.setText(content);
            mailSender.send(message);
            System.out.println("Activation email sent to:" + toEmail);
        } catch (Exception e) {
            System.err.println("ERROR SENDING ACTIVATION EMAIL FOR " + toEmail + ": " + e.getMessage());
        }
    }

    
    public void sendRegistrationSuccessEmail(String toEmail, String recipientName, String accountType) {
        String userName = recipientName != null && !recipientName.isEmpty() ? recipientName : "User";
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to the system!");

            String content = "Welcome " + userName + ",\n\n"
                           + "Congratulations! Your " + accountType + " account has been successfully registered.\n"
                           + "You can log in to the system now.\n\n"
                           + "Best regards,\n"
                           + "Recruitment team.";

            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error while sending registration success email: " + e.getMessage());
        }
    }
}