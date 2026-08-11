package com.example.bank.simulator1.service;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.bank.simulator1.dto.PaymentCallback;

@Component
public class BankCallbackClient {

    private final RestClient restClient;

    public BankCallbackClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public void sendCallback(
            String callbackUrl,
            PaymentCallback callback) {

        restClient.post()
                .uri(callbackUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(callback)
                .retrieve()
                .toBodilessEntity();
    }
}