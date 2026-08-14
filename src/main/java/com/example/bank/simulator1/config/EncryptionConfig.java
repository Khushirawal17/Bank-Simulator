package com.example.bank.simulator1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.bank.simulator1.security.EncryptionService;

@Configuration
public class EncryptionConfig {

    @Bean
    public EncryptionService encryptionService() {

        return new EncryptionService(
                "1234567890123456"
        );
    }
}