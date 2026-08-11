package com.example.bank.simulator1.security;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.config.BankProperties;
import com.example.bank.simulator1.dto.PaymentRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class ChecksumService {

	private final BankProperties bankProperties;

	public ChecksumService(BankProperties bankProperties) {
		this.bankProperties = bankProperties;
	}

	public boolean validatePaymentRequest(PaymentRequest request) {

		String generatedChecksum = generatePaymentChecksum(request);

		return generatedChecksum.equalsIgnoreCase(request.getCheckVal());
	}

	public String generatePaymentChecksum(PaymentRequest request) {

		String checksumKey = bankProperties.getSecurity().getChecksumKey();

		String input = String.join("|", safe(request.getMd()), safe(request.getPid()), safe(request.getNar()),
				safe(request.getPrn()), safe(request.getAmt()), safe(request.getCrn()), safe(request.getRu()),
				safe(checksumKey));

		return sha256(input);
	}

	private String sha256(String input) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

			StringBuilder result = new StringBuilder();

			for (byte value : hash) {
				result.append(String.format("%02x", value));
			}

			return result.toString();

		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 algorithm is not available", exception);
		}
	}

	private String safe(String value) {
		return value == null ? "" : value;
	}
}