package com.example.bank.simulator1.security;


import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.service.BankReferenceService;
import com.example.bank.simulator1.service.CallbackService;
import com.example.bank.simulator1.state.TransactionStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CallbackServiceTest {

    private final BankReferenceService bankReferenceService =
            new BankReferenceService();

    private final CallbackService callbackService =
            new CallbackService(bankReferenceService);

    @Test
    void shouldCreateSuccessfulCallback() {

        Transaction transaction =
                createTransaction(
                        TransactionStatus.SUCCESS
                );

        PaymentCallback callback =
                callbackService.buildCallback(
                        transaction
                );

        assertEquals("Y", callback.getStatus());
        assertEquals("TXN1001", callback.getPrn());
        assertEquals("100.00", callback.getAmt());
        assertEquals("", callback.getErrormsg());
        assertNotNull(callback.getBid());
        assertNotNull(callback.getDate());
    }

    @Test
    void shouldCreateFailureCallback() {

        Transaction transaction =
                createTransaction(
                        TransactionStatus.FAILURE
                );

        PaymentCallback callback =
                callbackService.buildCallback(
                        transaction
                );

        assertEquals("N", callback.getStatus());
        assertEquals(
                "Transaction failed",
                callback.getErrormsg()
        );
    }

    @Test
    void shouldCreatePendingCallback() {

        Transaction transaction =
                createTransaction(
                        TransactionStatus.PENDING
                );

        PaymentCallback callback =
                callbackService.buildCallback(
                        transaction
                );

        assertEquals("P", callback.getStatus());
        assertEquals(
                "",
                callback.getErrormsg()
        );
    }

    private Transaction createTransaction(
            TransactionStatus status) {

        Transaction transaction =
                new Transaction();

        transaction.setPrn("TXN1001");
        transaction.setMerchantName(
                "TESTMERCHANT"
        );
        transaction.setAmount(
                new BigDecimal("100.00")
        );
        transaction.setAccountNumber(
                "1234567890"
        );
        transaction.setStatus(status);

        return transaction;
    }
}