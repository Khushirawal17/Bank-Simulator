package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentRequest;

@Service
public class AuthenticationService {

	public boolean authenticate(PaymentRequest request) {

		/*
		 * Authentication is simulated.
		 *
		 * For the initial implementation, a valid payment request is considered
		 * authenticated.
		 *
		 * Bank-specific authentication rules can be added here when provided by the
		 * assigned bank.
		 */

		return request != null && request.getPrn() != null && !request.getPrn().isBlank();
	}
}