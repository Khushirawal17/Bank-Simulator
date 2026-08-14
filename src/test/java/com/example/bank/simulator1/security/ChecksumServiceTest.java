package com.example.bank.simulator1.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.config.BankProperties;
import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.dto.VerificationRequest;

class ChecksumServiceTest {

    private ChecksumService checksumService;

    @BeforeEach
    void setUp() {

        BankProperties bankProperties =
                new BankProperties();

        bankProperties.getSecurity()
                .setChecksumKey("TEST_SECRET");

        checksumService =
                new ChecksumService(bankProperties);
    }

    // =====================================================
    // PAYMENT CHECKSUM
    // =====================================================

    @Test
    void shouldGeneratePaymentChecksum() {

        PaymentRequest request =
                new PaymentRequest();

        request.setMd("P");
        request.setPid("TEST001");
        request.setNar("Test Payment");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setCrn("INR");
        request.setRu("http://localhost:9090/callback");

        String checksum =
                checksumService.generatePaymentChecksum(request);

        assertNotNull(checksum);
        assertEquals(64, checksum.length());
    }

    @Test
    void shouldValidateCorrectPaymentChecksum() {

        PaymentRequest request =
                new PaymentRequest();

        request.setMd("P");
        request.setPid("TEST001");
        request.setNar("Test Payment");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setCrn("INR");
        request.setRu("http://localhost:9090/callback");

        String checksum =
                checksumService.generatePaymentChecksum(request);

        request.setCheckVal(checksum);

        boolean result =
                checksumService.validatePaymentRequest(request);

        assertTrue(result);
    }

    @Test
    void shouldRejectIncorrectPaymentChecksum() {

        PaymentRequest request =
                new PaymentRequest();

        request.setMd("P");
        request.setPid("TEST001");
        request.setNar("Test Payment");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setCrn("INR");
        request.setRu("http://localhost:9090/callback");

        request.setCheckVal("WRONG_CHECKSUM");

        boolean result =
                checksumService.validatePaymentRequest(request);

        assertFalse(result);
    }

    // =====================================================
    // VERIFICATION CHECKSUM
    // =====================================================

    @Test
    void shouldGenerateVerificationChecksum() {

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPid("TEST001");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setNar("Test Payment");
        request.setBid("BANK001");
        request.setCrn("INR");
        request.setDate("2026-08-12");
        request.setData("TEST_DATA");

        String checksum =
                checksumService
                        .generateVerificationChecksum(request);

        assertNotNull(checksum);
        assertEquals(64, checksum.length());
    }

    @Test
    void shouldValidateCorrectVerificationChecksum() {

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPid("TEST001");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setNar("Test Payment");
        request.setBid("BANK001");
        request.setCrn("INR");
        request.setDate("2026-08-12");
        request.setData("TEST_DATA");

        String checksum =
                checksumService
                        .generateVerificationChecksum(request);

        request.setCheckVal(checksum);

        boolean result =
                checksumService
                        .validateVerificationRequest(request);

        assertTrue(result);
    }

    @Test
    void shouldRejectIncorrectVerificationChecksum() {

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPid("TEST001");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setNar("Test Payment");
        request.setBid("BANK001");
        request.setCrn("INR");
        request.setDate("2026-08-12");
        request.setData("TEST_DATA");

        request.setCheckVal("WRONG_CHECKSUM");

        boolean result =
                checksumService
                        .validateVerificationRequest(request);

        assertFalse(result);
    }

    @Test
    void shouldAcceptChecksumIgnoringCase() {

        PaymentRequest request =
                new PaymentRequest();

        request.setMd("P");
        request.setPid("TEST001");
        request.setNar("Test Payment");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setCrn("INR");
        request.setRu("http://localhost:9090/callback");

        String checksum =
                checksumService
                        .generatePaymentChecksum(request);

        request.setCheckVal(checksum.toUpperCase());

        boolean result =
                checksumService
                        .validatePaymentRequest(request);

        assertTrue(result);
    }
}