package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BankReferenceService {

	public String generate() {

		return "BID-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}
}