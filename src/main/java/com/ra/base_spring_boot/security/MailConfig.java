package com.ra.base_spring_boot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
public class MailConfig {

    // --- CẤU HÌNH GỬI MAIL ---
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // 1. Thông tin Máy chủ SMTP
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); 

        mailSender.setUsername("phuthgce182003@fpt.edu.vn");
        mailSender.setPassword("phu123456789"); 

        // 3. Thiết lập Thuộc tính JavaMail
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // Giao thức truyền tải
        props.put("mail.smtp.auth", "true"); // Bật xác thực
        props.put("mail.smtp.starttls.enable", "true"); // Bật bảo mật STARTTLS
        props.put("mail.smtp.starttls.required", "true"); // Yêu cầu bảo mật
        props.put("mail.debug", "false"); // Tắt debug (hoặc bật "true" để kiểm tra lỗi)

        return mailSender;
    }
}