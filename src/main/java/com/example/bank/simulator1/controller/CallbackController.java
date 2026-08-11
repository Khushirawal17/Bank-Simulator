package com.example.bank.simulator1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bank.simulator1.dto.PaymentCallback;

@RestController
@RequestMapping("/bank")
public class CallbackController {

	@PostMapping("/callback")
	public ResponseEntity<PaymentCallback> receiveCallback(@RequestBody PaymentCallback callback) {

		System.out.println("===== CALLBACK RECEIVED =====");
		System.out.println("PRN       : " + callback.getPrn());
		System.out.println("Status    : " + callback.getStatus());
		System.out.println("Amount    : " + callback.getAmt());
		System.out.println("Account   : " + callback.getAccno());
		System.out.println("Bank Ref  : " + callback.getBid());
		System.out.println("Narration : " + callback.getNar());
		System.out.println("=============================");

		return ResponseEntity.ok(callback);
	}
}