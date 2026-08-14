package com.example.bank.simulator1.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.exception.InvalidChecksumException;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.exception.TransactionNotFoundException;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.service.VerificationService;
import com.example.bank.simulator1.state.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class VerficationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ChecksumService checksumService;

    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
        verificationService =
                new VerificationService(
                        transactionRepository,
                        checksumService
                );
    }

    // =====================================================
    // TEST 1: Successful verification
    // =====================================================

    @Test
    void shouldVerifySuccessfully() {

        VerificationRequest request = createRequest();

        Transaction transaction = createTransaction();

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertNotNull(response);

        assertEquals("Y", response.getStatus());
        assertEquals("PRN001", response.getPrn());
        assertEquals("100.00", response.getAmount());
        assertEquals(
                "Transaction successful",
                response.getMessage()
        );

        verify(checksumService)
                .validateVerificationRequest(request);

        verify(transactionRepository)
                .findByPrn("PRN001");
    }

    // =====================================================
    // TEST 2: Invalid MD
    // =====================================================

    @Test
    void shouldRejectInvalidMd() {

        VerificationRequest request = createRequest();

        request.setMd("P");

        assertThrows(
                InvalidRequestException.class,
                () -> verificationService.verify(request)
        );

        verifyNoInteractions(checksumService);
        verifyNoInteractions(transactionRepository);
    }

    // =====================================================
    // TEST 3: Null request
    // =====================================================

    @Test
    void shouldRejectNullRequest() {

        assertThrows(
                InvalidRequestException.class,
                () -> verificationService.verify(null)
        );

        verifyNoInteractions(checksumService);
        verifyNoInteractions(transactionRepository);
    }

    // =====================================================
    // TEST 4: Invalid checksum
    // =====================================================

    @Test
    void shouldRejectInvalidChecksum() {

        VerificationRequest request = createRequest();

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(false);

        assertThrows(
                InvalidChecksumException.class,
                () -> verificationService.verify(request)
        );

        verify(checksumService)
                .validateVerificationRequest(request);

        verifyNoInteractions(transactionRepository);
    }

    // =====================================================
    // TEST 5: Transaction not found
    // =====================================================

    @Test
    void shouldRejectUnknownPrn() {

        VerificationRequest request = createRequest();

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> verificationService.verify(request)
        );

        verify(transactionRepository)
                .findByPrn("PRN001");
    }

    // =====================================================
    // TEST 6: PID mismatch
    // =====================================================

    @Test
    void shouldFailWhenPidDoesNotMatch() {

        VerificationRequest request = createRequest();

        Transaction transaction = createTransaction();

        transaction.setPayeeId("DIFFERENT_PID");

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("N", response.getStatus());
        assertEquals("PRN001", response.getPrn());
        assertEquals("100.00", response.getAmount());

        assertEquals(
                "Verification details do not match payment transaction",
                response.getMessage()
        );
    }

    // =====================================================
    // TEST 7: Amount mismatch
    // =====================================================

    @Test
    void shouldFailWhenAmountDoesNotMatch() {

        VerificationRequest request = createRequest();

        request.setAmt("200.00");

        Transaction transaction = createTransaction();

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("N", response.getStatus());
        assertEquals("PRN001", response.getPrn());

        assertEquals(
                "Verification details do not match payment transaction",
                response.getMessage()
        );
    }

    // =====================================================
    // TEST 8: Narration mismatch
    // =====================================================

    @Test
    void shouldFailWhenNarrationDoesNotMatch() {

        VerificationRequest request = createRequest();

        request.setNar("Different Narration");

        Transaction transaction = createTransaction();

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("N", response.getStatus());

        assertEquals(
                "Verification details do not match payment transaction",
                response.getMessage()
        );
    }

    // =====================================================
    // TEST 9: Currency mismatch
    // =====================================================

    @Test
    void shouldFailWhenCurrencyDoesNotMatch() {

        VerificationRequest request = createRequest();

        request.setCrn("USD");

        Transaction transaction = createTransaction();

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("N", response.getStatus());

        assertEquals(
                "Verification details do not match payment transaction",
                response.getMessage()
        );
    }

    // =====================================================
    // TEST 10: Failure transaction
    // =====================================================

    @Test
    void shouldReturnFailureStatusForFailedTransaction() {

        VerificationRequest request = createRequest();

        Transaction transaction = createTransaction();

        transaction.setStatus(TransactionStatus.FAILURE);

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("N", response.getStatus());
        assertEquals(
                "Transaction failed",
                response.getMessage()
        );
    }

    // =====================================================
    // TEST 11: Pending transaction
    // =====================================================

    @Test
    void shouldReturnPendingStatusForPendingTransaction() {

        VerificationRequest request = createRequest();

        Transaction transaction = createTransaction();

        transaction.setStatus(TransactionStatus.PENDING);

        when(checksumService.validateVerificationRequest(request))
                .thenReturn(true);

        when(transactionRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(transaction));

        VerificationResponse response =
                verificationService.verify(request);

        assertEquals("P", response.getStatus());
        assertEquals(
                "Transaction pending",
                response.getMessage()
        );
    }

    // =====================================================
    // HELPER: Verification request
    // =====================================================

    private VerificationRequest createRequest() {

        VerificationRequest request =
                new VerificationRequest();

        request.setMd("V");
        request.setPid("TEST001");
        request.setPrn("PRN001");
        request.setAmt("100.00");
        request.setNar("Test Payment");
        request.setCrn("INR");

        return request;
    }

    // =====================================================
    // HELPER: Transaction
    // =====================================================

    private Transaction createTransaction() {

        Transaction transaction =
                new Transaction();

        transaction.setPrn("PRN001");
        transaction.setPayeeId("TEST001");
        transaction.setAmount(
                new BigDecimal("100.00")
        );
        transaction.setMerchantName("Test Payment");
        transaction.setCurrency("INR");
        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        return transaction;
    }
}