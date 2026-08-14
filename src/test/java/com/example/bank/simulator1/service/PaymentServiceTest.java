package com.example.bank.simulator1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.dto.PaymentResponse;
import com.example.bank.simulator1.exception.InvalidChecksumException;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.security.ChecksumService;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ChecksumService checksumService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SimulationService simulationService;

    @Mock
    private CallbackService callbackService;

    @Mock
    private CallbackBehaviorService callbackBehaviourService;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {

        paymentService =
                new PaymentService(
                        transactionRepository,
                        checksumService,
                        authenticationService,
                        simulationService,
                        callbackService,
                        callbackBehaviourService
                );
    }

    // =====================================================
    // TEST 1: SUCCESSFUL PAYMENT
    // =====================================================

    @Test
    void shouldProcessSuccessfulPayment() {

        PaymentRequest request = createValidRequest();

        when(transactionRepository.existsByPrn("PRN001"))
                .thenReturn(false);

        when(checksumService.generatePaymentChecksum(request))
                .thenReturn("VALID_CHECKSUM");

        when(authenticationService.authenticate(request))
                .thenReturn(true);

        when(simulationService.determineStatus("PRN001"))
                .thenReturn(TransactionStatus.SUCCESS);

        SimulationConfig config =
                new SimulationConfig(
                        "PRN001",
                        SimulationMode.SUCCESS,
                        0
                );

        when(simulationService.getConfiguration("PRN001"))
                .thenReturn(config);

        PaymentCallback callback =
                new PaymentCallback();

        callback.setPrn("PRN001");

        when(callbackService.buildCallback(any(Transaction.class)))
                .thenReturn(callback);

        Transaction savedTransaction =
                new Transaction();

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        request.setCheckVal("VALID_CHECKSUM");

        PaymentResponse response =
                paymentService.processPayment(request);

        // -------------------------------------------------
        // Response assertions
        // -------------------------------------------------

        assertNotNull(response);

        assertEquals(
                "Y",
                response.getStatus()
        );

        assertEquals(
                "PRN001",
                response.getPrn()
        );

        assertEquals(
                "Test Payment",
                response.getNar()
        );

        assertEquals(
                "100.00",
                response.getAmt()
        );

        assertEquals(
                "1234567890",
                response.getAccno()
        );

        // -------------------------------------------------
        // Verify important service calls
        // -------------------------------------------------

        verify(transactionRepository)
                .existsByPrn("PRN001");

        verify(checksumService)
                .generatePaymentChecksum(request);

        verify(authenticationService)
                .authenticate(request);

        verify(simulationService)
                .determineStatus("PRN001");

        verify(transactionRepository)
                .save(any(Transaction.class));

        verify(callbackService)
                .buildCallback(any(Transaction.class));

        verify(simulationService)
                .getConfiguration("PRN001");

        verify(callbackBehaviourService)
                .execute(
                        eq("http://localhost:3000/callback"),
                        eq(callback),
                        eq(config)
                );
    }

    // =====================================================
    // TEST 2: FAILURE WHEN AUTHENTICATION FAILS
    // =====================================================

    @Test
    void shouldReturnFailureWhenAuthenticationFails() {

        PaymentRequest request =
                createValidRequest();

        when(transactionRepository.existsByPrn("PRN001"))
                .thenReturn(false);

        when(checksumService.generatePaymentChecksum(request))
                .thenReturn("VALID_CHECKSUM");

        when(authenticationService.authenticate(request))
                .thenReturn(false);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        SimulationConfig config =
                new SimulationConfig(
                        "PRN001",
                        SimulationMode.FAILURE,
                        0
                );

        when(simulationService.getConfiguration("PRN001"))
                .thenReturn(config);

        PaymentCallback callback =
                new PaymentCallback();

        callback.setPrn("PRN001");

        when(callbackService.buildCallback(any(Transaction.class)))
                .thenReturn(callback);

        request.setCheckVal("VALID_CHECKSUM");

        PaymentResponse response =
                paymentService.processPayment(request);

        assertNotNull(response);

        assertEquals(
                "N",
                response.getStatus()
        );

        assertEquals(
                "PRN001",
                response.getPrn()
        );

        verify(authenticationService)
                .authenticate(request);

        verify(transactionRepository)
                .save(any(Transaction.class));

        verify(callbackService)
                .buildCallback(any(Transaction.class));

        verify(callbackBehaviourService)
                .execute(
                        eq("http://localhost:3000/callback"),
                        eq(callback),
                        eq(config)
                );

        // Simulation status should NOT be called
        // because authentication failed.
        verify(
                simulationService,
                never()
        ).determineStatus("PRN001");
    }

    // =====================================================
    // TEST 3: PENDING PAYMENT
    // =====================================================

    @Test
    void shouldProcessPendingPayment() {

        PaymentRequest request =
                createValidRequest();

        when(transactionRepository.existsByPrn("PRN001"))
                .thenReturn(false);

        when(checksumService.generatePaymentChecksum(request))
                .thenReturn("VALID_CHECKSUM");

        when(authenticationService.authenticate(request))
                .thenReturn(true);

        when(simulationService.determineStatus("PRN001"))
                .thenReturn(TransactionStatus.PENDING);

        SimulationConfig config =
                new SimulationConfig(
                        "PRN001",
                        SimulationMode.PENDING,
                        0
                );

        when(simulationService.getConfiguration("PRN001"))
                .thenReturn(config);

        PaymentCallback callback =
                new PaymentCallback();

        callback.setPrn("PRN001");

        when(callbackService.buildCallback(any(Transaction.class)))
                .thenReturn(callback);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        request.setCheckVal("VALID_CHECKSUM");

        PaymentResponse response =
                paymentService.processPayment(request);

        assertNotNull(response);

        assertEquals(
                "P",
                response.getStatus()
        );

        assertEquals(
                "PRN001",
                response.getPrn()
        );

        verify(simulationService)
                .determineStatus("PRN001");

        verify(transactionRepository)
                .save(any(Transaction.class));

        verify(callbackBehaviourService)
                .execute(
                        eq("http://localhost:3000/callback"),
                        eq(callback),
                        eq(config)
                );
    }



    // =====================================================
    // TEST 4: INVALID CHECKSUM
    // =====================================================

    @Test
    void shouldRejectPaymentWhenChecksumIsInvalid() {

        PaymentRequest request =
                createValidRequest();

        when(transactionRepository.existsByPrn("PRN001"))
                .thenReturn(false);

        when(checksumService.generatePaymentChecksum(request))
                .thenReturn("CORRECT_CHECKSUM");

        request.setCheckVal(
                "WRONG_CHECKSUM"
        );

        assertThrows(
                InvalidChecksumException.class,
                () -> paymentService.processPayment(request)
        );

        // Payment should stop before authentication.
        verify(
                authenticationService,
                never()
        ).authenticate(any());

        // Transaction should not be saved.
        verify(
                transactionRepository,
                never()
        ).save(any(Transaction.class));

        // Callback should not happen.
        verify(
                callbackService,
                never()
        ).buildCallback(any(Transaction.class));
    }

    
    // =====================================================
    // TEST 5: WRONG MD
    // =====================================================

    @Test
    void shouldRejectRequestWithWrongMd() {

        PaymentRequest request =
                createValidRequest();

        request.setMd("V");

        assertThrows(
                InvalidRequestException.class,
                () -> paymentService.processPayment(request)
        );

        verify(
                checksumService,
                never()
        ).generatePaymentChecksum(any());

        verify(
                authenticationService,
                never()
        ).authenticate(any());
    }

    // =====================================================
    // TEST 6: PRN IS BLANK
    // =====================================================

    @Test
    void shouldRejectRequestWithBlankPrn() {

        PaymentRequest request =
                createValidRequest();

        request.setPrn("");

        assertThrows(
                InvalidRequestException.class,
                () -> paymentService.processPayment(request)
        );

        verify(
                transactionRepository,
                never()
        ).existsByPrn(anyString());
    }

    // =====================================================
    // TEST : DUPLICATE PRN
    // =====================================================

    @Test
    void shouldRejectDuplicatePrn() {

        PaymentRequest request =
                createValidRequest();

        when(transactionRepository.existsByPrn("PRN001"))
                .thenReturn(true);

        assertThrows(
                InvalidRequestException.class,
                () -> paymentService.processPayment(request)
        );

        verify(transactionRepository)
                .existsByPrn("PRN001");

        verify(
                checksumService,
                never()
        ).generatePaymentChecksum(any());

        verify(
                transactionRepository,
                never()
        ).save(any(Transaction.class));
    }


    // =====================================================
    // HELPER METHOD
    // =====================================================

    private PaymentRequest createValidRequest() {

        PaymentRequest request =
                new PaymentRequest();

        request.setMd("P");

        request.setPid(
                "TEST001"
        );

        request.setNar(
                "Test Payment"
        );

        request.setPrn(
                "PRN001"
        );

        request.setAmt(
                "100.00"
        );

        request.setCrn(
                "INR"
        );

        request.setRu(
                "http://localhost:3000/callback"
        );

        request.setAccno(
                "1234567890"
        );

        request.setDate(
                "2026-08-12T16:00:00.000"
        );

        return request;
    }
}