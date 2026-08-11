package com.example.bank.simulator1.security;


import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.service.AuthenticationService;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private final AuthenticationService authenticationService =
            new AuthenticationService();

    @Test
    void shouldAuthenticateValidRequest() {

        PaymentRequest request = new PaymentRequest();
        request.setPrn("TXN1001");

        assertTrue(
                authenticationService.authenticate(request)
        );
    }

    @Test
    void shouldRejectRequestWithoutPrn() {

        PaymentRequest request = new PaymentRequest();

        assertFalse(
                authenticationService.authenticate(request)
        );
    }

    @Test
    void shouldRejectNullRequest() {

        assertFalse(
                authenticationService.authenticate(null)
        );
    }
}
