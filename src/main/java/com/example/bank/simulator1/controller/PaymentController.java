package com.example.bank.simulator1.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.dto.PaymentResponse;
import com.example.bank.simulator1.service.PaymentService;

@RestController
@RequestMapping("/bank")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(request);

        return ResponseEntity.ok(response);
    }
}