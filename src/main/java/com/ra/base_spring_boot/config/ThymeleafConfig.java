package com.ra.base_spring_boot.config;

import org.springframework.beans.factory.annotation.Value; // Import mới
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafConfig {

   
    @Value("${spring.thymeleaf.cache:true}")
    private boolean isCacheable;

    @Bean
    public ClassLoaderTemplateResolver cvTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();


        templateResolver.setPrefix("templates/cv/");
        templateResolver.setSuffix(".html");


        templateResolver.setTemplateMode(TemplateMode.HTML);


        templateResolver.setCacheable(isCacheable);

        return templateResolver;
    }

    @Bean(name = "pdfTemplateEngine")
    public TemplateEngine pdfTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(cvTemplateResolver());


        return templateEngine;
    }
}