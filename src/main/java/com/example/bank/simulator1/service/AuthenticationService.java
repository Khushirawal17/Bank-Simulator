package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentRequest;

@Service
public class AuthenticationService {

	public boolean authenticate(PaymentRequest request) {

		
		return request != null && request.getPrn() != null && !request.getPrn().isBlank();
	}
}