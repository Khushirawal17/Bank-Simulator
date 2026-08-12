package com.example.bank.simulator1.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.config.BankProperties;
import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.dto.VerificationRequest;

@Service
public class ChecksumService {

    private final BankProperties bankProperties;

    public ChecksumService(BankProperties bankProperties) {
        this.bankProperties = bankProperties;
    }

    // =========================================================
    // PAYMENT CHECKSUM
    // =========================================================

    public boolean validatePaymentRequest(PaymentRequest request) {

        String generatedChecksum =
                generatePaymentChecksum(request);

        return generatedChecksum.equalsIgnoreCase(
                request.getCheckVal()
        );
    }

    public String generatePaymentChecksum(
            PaymentRequest request) {

        String checksumKey =
                bankProperties.getSecurity().getChecksumKey();

        String input = String.join(
                "|",
                safe(request.getMd()),
                safe(request.getPid()),
                safe(request.getNar()),
                safe(request.getPrn()),
                safe(request.getAmt()),
                safe(request.getCrn()),
                safe(request.getRu()),
                safe(checksumKey)
        );

        return sha256(input);
    }

    // =========================================================
    // VERIFICATION CHECKSUM
    // =========================================================

    public boolean validateVerificationRequest(
            VerificationRequest request) {

        String generatedChecksum =
                generateVerificationChecksum(request);

        return generatedChecksum.equalsIgnoreCase(
                request.getCheckVal()
        );
    }

    public String generateVerificationChecksum(
            VerificationRequest request) {

        String checksumKey =
                bankProperties.getSecurity().getChecksumKey();

        String input = String.join(
                "|",
                safe(request.getMd()),
                safe(request.getPid()),
                safe(request.getPrn()),
                safe(request.getAmt()),
                safe(request.getNar()),
                safe(request.getBid()),
                safe(request.getCrn()),
                safe(request.getDate()),
                safe(request.getData()),
                safe(checksumKey)
        );

        System.out.println("=================================");
        System.out.println("VERIFICATION CHECKSUM INPUT:");
        System.out.println(input);

        String checksum = sha256(input);

        System.out.println("VERIFICATION GENERATED CHECKSUM:");
        System.out.println(checksum);
        System.out.println("=================================");

        return checksum;
    }

    // =========================================================
    // SHA-256
    // =========================================================

    private String sha256(String input) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            input.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte value : hash) {

                result.append(
                        String.format("%02x", value)
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }

    private String safe(String value) {

        return value == null ? "" : value;
    }
}