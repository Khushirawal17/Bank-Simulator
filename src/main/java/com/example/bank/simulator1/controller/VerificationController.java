package com.example.bank.simulator1.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.service.DoubleVerificationService;

@RestController
@RequestMapping("/bank")
public class VerificationController {

    private final DoubleVerificationService
            doubleVerificationService;

    public VerificationController(
            DoubleVerificationService
                    doubleVerificationService) {

        this.doubleVerificationService =
                doubleVerificationService;
    }

    @PostMapping("/verification")
    public ResponseEntity<VerificationResponse> verify(
            @Valid
            @RequestBody
            VerificationRequest request) {

        VerificationResponse response =
                doubleVerificationService.verify(request);

        return ResponseEntity.ok(response);
    }
}