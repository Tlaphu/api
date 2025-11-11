package com.ra.base_spring_boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VNPayProperties {
    private String tmnCode;
    private String hashSecret;
    private String url;
    private String returnUrl;
}