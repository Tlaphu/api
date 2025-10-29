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
   
 public void sendResetPasswordEmail(String toEmail, String recipientName, String resetLink) {
    String userName = recipientName != null && !recipientName.isEmpty() ? recipientName : "User";
    try {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");

        String content = "Dear " + userName + ",\n\n"
                       + "You requested a password reset. Please click the link below to set a new password:\n\n"
                       + resetLink + "\n\n"
                       + "This link will expire soon. If you didn't request this, please ignore this email.\n\n"
                       + "Best regards,\n"
                       + "The Recruitment Team.";

        message.setText(content);
        mailSender.send(message);
        System.out.println("Password reset email sent to: " + toEmail);
    } catch (Exception e) {
        System.err.println("ERROR SENDING RESET PASSWORD EMAIL to " + toEmail + ": " + e.getMessage());
    }
}
public void sendLoginCredentialsEmail(String toEmail, String recipientName, String defaultPassword) {
    String userName = recipientName != null && !recipientName.isEmpty() ? recipientName : "User";
    try {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("✅ Your Account Has Been Activated! - Login Credentials");

        String content = "Dear " + userName + ",\n\n"
                       + "Good news! Your account has been approved and activated by the Administrator.\n"
                       + "You can now log in using the following credentials:\n\n"
                       + "  - **Email:** " + toEmail + "\n"
                       + "  - **Initial Password:** " + defaultPassword + "\n\n"
                       + "⚠️ **Security Notice:** We highly recommend that you change your password immediately after your first successful login.\n\n"
                       + "Best regards,\n"
                       + "The Recruitment Team.";

        message.setText(content);
        mailSender.send(message);
        System.out.println("Login credentials email sent to: " + toEmail);
    } catch (Exception e) {
        System.err.println("ERROR SENDING LOGIN CREDENTIALS EMAIL to " + toEmail + ": " + e.getMessage());
    }
}
}