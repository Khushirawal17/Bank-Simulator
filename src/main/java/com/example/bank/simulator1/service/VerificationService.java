package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.exception.TransactionNotFoundException;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class VerificationService {

    private final TransactionRepository transactionRepository;

    public VerificationService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public VerificationResponse verify(
            VerificationRequest request) {

        validateRequest(request);

        Transaction transaction =
                transactionRepository
                        .findByPrn(request.getPrn())
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Transaction not found for PRN: "
                                                + request.getPrn()
                                )
                        );

        return buildResponse(transaction);
    }

    private void validateRequest(
            VerificationRequest request) {

        if (!"V".equalsIgnoreCase(request.getMd())) {
            throw new InvalidRequestException(
                    "MD must be V for a verification request"
            );
        }
    }

    private VerificationResponse buildResponse(
            Transaction transaction) {

        VerificationResponse response =
                new VerificationResponse();

        response.setStatus(
                mapStatus(transaction.getStatus())
        );

        response.setPrn(transaction.getPrn());

        if (transaction.getAmount() != null) {
            response.setAmount(
                    transaction.getAmount().toPlainString()
            );
        }

        response.setMessage(
                getStatusMessage(transaction.getStatus())
        );

        return response;
    }

    private String mapStatus(
            TransactionStatus status) {

        return switch (status) {
            case SUCCESS -> "Y";
            case FAILURE -> "N";
            case PENDING -> "P";
            default -> "P";
        };
    }

    private String getStatusMessage(
            TransactionStatus status) {

        return switch (status) {
            case SUCCESS -> "Transaction successful";
            case FAILURE -> "Transaction failed";
            case PENDING -> "Transaction pending";
            default -> "Transaction is being processed";
        };
    }
}