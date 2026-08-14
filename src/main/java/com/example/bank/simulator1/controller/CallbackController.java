package com.example.bank.simulator1.controller;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.service.DoubleVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/bank")
public class CallbackController {

	private static final String ALGORITHM = "AES/GCM/NoPadding";

	private static final int GCM_TAG_LENGTH = 128;

	private static final int IV_LENGTH = 12;

	private final DoubleVerificationService doubleVerificationService;

	private final ObjectMapper objectMapper;

	private final SecretKeySpec secretKey;

	public CallbackController(
	        DoubleVerificationService doubleVerificationService,
	        @Value("${bank.encryption.secret}") String secret) {

	    this.doubleVerificationService = doubleVerificationService;

	    this.objectMapper = new ObjectMapper();

	    this.secretKey = new SecretKeySpec(
	        secret.getBytes(StandardCharsets.UTF_8),
	        "AES"
	    );
	}

	@PostMapping(
            value = "/callback",
            consumes = MediaType.TEXT_PLAIN_VALUE
    )
	public ResponseEntity<PaymentCallback> receiveCallback(@RequestBody String encryptedPayload) {

		try {

			// ==========================================
			// 1. Decrypt callback
			// ==========================================

			String jsonPayload = decrypt(encryptedPayload);
			
			System.out.print(jsonPayload);

			// ==========================================
			// 2. Convert JSON back to PaymentCallback
			// ==========================================

			PaymentCallback callback = objectMapper.readValue(jsonPayload, PaymentCallback.class);

			// ==========================================
			// 3. Existing callback processing
			// ==========================================

			System.out.println();
			System.out.println("===== CALLBACK RECEIVED =====");

			System.out.println("PRN       : " + callback.getPrn());

			System.out.println("Status    : " + callback.getStatus());

			System.out.println("Amount    : " + callback.getAmt());

			System.out.println("Account   : " + callback.getAccno());

			System.out.println("Bank Ref  : " + callback.getBid());

			System.out.println("Narration : " + callback.getNar());

			System.out.println("=============================");

			VerificationResponse verificationResponse = doubleVerificationService.verifyCallback(callback);

			System.out.println();
			System.out.println("===== FINAL PAYMENT RESULT =====");

			System.out.println("PRN        : " + verificationResponse.getPrn());

			System.out.println("Status     : " + verificationResponse.getStatus());

			System.out.println("Amount     : " + verificationResponse.getAmount());

			System.out.println("Message    : " + verificationResponse.getMessage());

			System.out.println("================================");

			System.out.println();

			return ResponseEntity.ok(callback);

		} catch (Exception exception) {

			exception.printStackTrace();

			return ResponseEntity.badRequest().build();
		}
	}

	private String decrypt(String encryptedPayload) {

		try {

			// Remove whitespace/newlines if present
			encryptedPayload = encryptedPayload.trim();

			// Base64 decode
			byte[] combined = Base64.getDecoder().decode(encryptedPayload);

			// ==========================================
			// Extract IV
			// ==========================================

			byte[] iv = new byte[IV_LENGTH];

			System.arraycopy(combined, 0, iv, 0, IV_LENGTH);

			// ==========================================
			// Extract encrypted data
			// ==========================================

			byte[] encrypted = new byte[combined.length - IV_LENGTH];

			System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

			// ==========================================
			// Create AES-GCM cipher
			// ==========================================

			Cipher cipher = Cipher.getInstance(ALGORITHM);

			GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

			// ==========================================
			// Decrypt
			// ==========================================

			byte[] decrypted = cipher.doFinal(encrypted);

			return new String(decrypted, StandardCharsets.UTF_8);

		} catch (Exception exception) {

			throw new IllegalStateException("Unable to decrypt callback", exception);
		}
	}
}