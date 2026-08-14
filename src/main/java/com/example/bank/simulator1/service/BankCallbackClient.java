package com.example.bank.simulator1.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.security.EncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BankCallbackClient {

    private final RestClient restClient;

    private final EncryptionService encryptionService;

    private final ObjectMapper objectMapper;

    public BankCallbackClient(
            RestClient.Builder builder,
            EncryptionService encryptionService) {

        this.restClient = builder.build();

        this.encryptionService =
                encryptionService;

        this.objectMapper =
                new ObjectMapper();
    
    }

    public void sendCallback(
            String callbackUrl,
            PaymentCallback callback) {

        try {

            // Convert callback object to JSON
            String jsonPayload =
                    objectMapper.writeValueAsString(
                            callback
                    );

            // Encrypt JSON payload
            String encryptedPayload =
                    encryptionService.encrypt(
                            jsonPayload
                    );
            
            System.out.println("========== ENCRYPTED CALLBACK ==========");
            System.out.println(encryptedPayload);
            System.out.println("========================================");

            // Send encrypted payload
            restClient.post()
                    .uri(callbackUrl)
                    .contentType(
                            MediaType.TEXT_PLAIN
                    )
                    .body(encryptedPayload)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to send encrypted callback",
                    exception
            );
        }
    }
}