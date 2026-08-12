package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class DoubleVerificationService {

    private final VerificationService verificationService;

    public DoubleVerificationService(
            VerificationService verificationService) {

        this.verificationService = verificationService;
    }

    public boolean verify(String prn) {

        VerificationRequest firstRequest =
                createVerificationRequest(prn);

        VerificationResponse firstResponse =
                verificationService.verify(firstRequest);

        if (!"Y".equalsIgnoreCase(firstResponse.getStatus())) {
            return false;
        }

        VerificationRequest secondRequest =
                createVerificationRequest(prn);

        VerificationResponse secondResponse =
                verificationService.verify(secondRequest);

        return "Y".equalsIgnoreCase(secondResponse.getStatus());
    }

    private VerificationRequest createVerificationRequest(
            String prn) {

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPrn(prn);

        return request;
    }
}