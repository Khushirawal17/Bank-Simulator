package com.example.bank.simulator1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.security.ChecksumService;

@SpringBootTest
class BankSimulator1ApplicationTests {

    @Autowired
    private ChecksumService checksumService;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldGeneratePaymentChecksum() {

        PaymentRequest request = new PaymentRequest();

        request.setMd("P");
        request.setPid("TEST001");
        request.setNar("Test Payment");
        request.setPrn("PRN018");
        request.setAmt("100.00");
        request.setCrn("INR");
        request.setRu("http://localhost:9090/bank/callback");

        String checksum =
                checksumService.generatePaymentChecksum(request);

        assertNotNull(checksum);
        assertEquals(64, checksum.length());
    }
}