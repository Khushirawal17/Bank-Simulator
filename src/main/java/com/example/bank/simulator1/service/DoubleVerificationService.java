package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.security.ChecksumService;

@Service
public class DoubleVerificationService {

    private final VerificationService verificationService;
    private final ChecksumService checksumService;

    public DoubleVerificationService(
            VerificationService verificationService,
            ChecksumService checksumService) {

        this.verificationService = verificationService;
        this.checksumService = checksumService;
    }

    public VerificationResponse verifyCallback(
            PaymentCallback callback) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       DOUBLE VERIFICATION START");
        System.out.println("==========================================");

        System.out.println("PRN       : " + callback.getPrn());
        System.out.println("Status    : " + callback.getStatus());
        System.out.println("Amount    : " + callback.getAmt());
        System.out.println("Narration : " + callback.getNar());

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPid(callback.getPid());
        request.setPrn(callback.getPrn());
        request.setAmt(callback.getAmt());
        request.setNar(callback.getNar());
        request.setBid(callback.getBid());
        request.setCrn("INR");

        
        String verificationChecksum =
                checksumService.generateVerificationChecksum(request);

        request.setCheckVal(verificationChecksum);

        System.out.println("------------------------------------------");
        System.out.println("VERIFICATION REQUEST CREATED");
        System.out.println("------------------------------------------");

        System.out.println("MD        : " + request.getMd());
        System.out.println("PID       : " + request.getPid());
        System.out.println("PRN       : " + request.getPrn());
        System.out.println("AMT       : " + request.getAmt());
        System.out.println("NAR       : " + request.getNar());
        System.out.println("BID       : " + request.getBid());
        System.out.println("CRN       : " + request.getCrn());
        System.out.println("CHECKVAL  : " + request.getCheckVal());

        // -------------------------------------------------
        // STEP 3: Verify request
        // -------------------------------------------------

        System.out.println("------------------------------------------");
        System.out.println("Calling VerificationService...");
        System.out.println("------------------------------------------");

        VerificationResponse response =
                verificationService.verify(request);

        // -------------------------------------------------
        // STEP 4: Print response
        // -------------------------------------------------

        System.out.println("------------------------------------------");
        System.out.println("VERIFICATION RESPONSE");
        System.out.println("------------------------------------------");

        System.out.println("PRN       : " + response.getPrn());
        System.out.println("Status    : " + response.getStatus());
        System.out.println("Amount    : " + response.getAmount());
        System.out.println("Message   : " + response.getMessage());

        if ("Y".equalsIgnoreCase(response.getStatus())) {

            System.out.println("------------------------------------------");
            System.out.println("DOUBLE VERIFICATION SUCCESS");

        } else {

            System.out.println("------------------------------------------");
            System.out.println("DOUBLE VERIFICATION FAILED");
        }

        System.out.println("==========================================");
        System.out.println("        DOUBLE VERIFICATION END");
        System.out.println("==========================================");
        System.out.println();

        return response;
    }
}