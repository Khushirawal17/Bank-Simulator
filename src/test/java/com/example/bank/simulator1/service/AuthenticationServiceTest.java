package com.example.bank.simulator1.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.dto.PaymentRequest;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService =
                new AuthenticationService();
    }

    // =====================================================
    // TEST 1: Valid PRN
    // =====================================================

    @Test
    void shouldAuthenticateValidPaymentRequest() {

        PaymentRequest request =
                new PaymentRequest();

        request.setPrn("PRN001");

        boolean result =
                authenticationService.authenticate(request);

        assertTrue(result);
    }

    // =====================================================
    // TEST 2: Null request
    // =====================================================

    @Test
    void shouldRejectNullRequest() {

        boolean result =
                authenticationService.authenticate(null);

        assertFalse(result);
    }

    // =====================================================
    // TEST 3: Null PRN
    // =====================================================

    @Test
    void shouldRejectRequestWithNullPrn() {

        PaymentRequest request =
                new PaymentRequest();

        request.setPrn(null);

        boolean result =
                authenticationService.authenticate(request);

        assertFalse(result);
    }

    // =====================================================
    // TEST 4: Empty PRN
    // =====================================================

    @Test
    void shouldRejectRequestWithEmptyPrn() {

        PaymentRequest request =
                new PaymentRequest();

        request.setPrn("");

        boolean result =
                authenticationService.authenticate(request);

        assertFalse(result);
    }

    // =====================================================
    // TEST 5: Blank PRN
    // =====================================================

    @Test
    void shouldRejectRequestWithBlankPrn() {

        PaymentRequest request =
                new PaymentRequest();

        request.setPrn("   ");

        boolean result =
                authenticationService.authenticate(request);

        assertFalse(result);
    }

    // =====================================================
    // TEST 6: Different valid PRN
    // =====================================================

    @Test
    void shouldAuthenticateDifferentValidPrn() {

        PaymentRequest request =
                new PaymentRequest();

        request.setPrn("PRN999");

        boolean result =
                authenticationService.authenticate(request);

        assertTrue(result);
    }
}