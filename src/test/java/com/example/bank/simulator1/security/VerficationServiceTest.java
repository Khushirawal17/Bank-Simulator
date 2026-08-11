package com.example.bank.simulator1.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.exception.TransactionNotFoundException;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.InMemoryTransactionRepository;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.service.VerificationService;
import com.example.bank.simulator1.state.TransactionStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

    private TransactionRepository transactionRepository;
    private VerificationService verificationService;

    @BeforeEach
    void setUp() {

        transactionRepository =
                new InMemoryTransactionRepository();

        verificationService =
                new VerificationService(
                        transactionRepository
                );
    }

    @Test
    void shouldReturnSuccessfulTransaction() {

        Transaction transaction =
                createTransaction(
                        "TXN1001",
                        TransactionStatus.SUCCESS
                );

        transactionRepository.save(transaction);

        VerificationRequest request =
                createRequest("TXN1001");

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("Y", response.getStatus());
        assertEquals("TXN1001", response.getPrn());
        assertEquals("100.00", response.getAmount());
    }

    @Test
    void shouldReturnFailedTransaction() {

        Transaction transaction =
                createTransaction(
                        "TXN1002",
                        TransactionStatus.FAILURE
                );

        transactionRepository.save(transaction);

        VerificationResponse response =
                verificationService.verify(
                        createRequest("TXN1002")
                );

        assertEquals("N", response.getStatus());
    }

    @Test
    void shouldReturnPendingTransaction() {

        Transaction transaction =
                createTransaction(
                        "TXN1003",
                        TransactionStatus.PENDING
                );

        transactionRepository.save(transaction);

        VerificationResponse response =
                verificationService.verify(
                        createRequest("TXN1003")
                );

        assertEquals("P", response.getStatus());
    }

    @Test
    void shouldRejectNonVerificationRequest() {

        VerificationRequest request =
                createRequest("TXN1001");

        request.setMd("P");

        assertThrows(
                InvalidRequestException.class,
                () -> verificationService.verify(request)
        );
    }

    @Test
    void shouldThrowExceptionForUnknownTransaction() {

        VerificationRequest request =
                createRequest("UNKNOWN");

        assertThrows(
                TransactionNotFoundException.class,
                () -> verificationService.verify(request)
        );
    }

    private Transaction createTransaction(
            String prn,
            TransactionStatus status) {

        Transaction transaction =
                new Transaction();

        transaction.setPrn(prn);

        transaction.setAmount(
                new BigDecimal("100.00")
        );

        transaction.setStatus(status);

        return transaction;
    }

    private VerificationRequest createRequest(
            String prn) {

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPid("TEST001");
        request.setPrn(prn);

        return request;
    }
}