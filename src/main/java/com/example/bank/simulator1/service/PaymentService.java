package com.example.bank.simulator1.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.dto.PaymentResponse;
import com.example.bank.simulator1.exception.InvalidChecksumException;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.security.ChecksumService;
import com.example.bank.simulator1.state.PaymentMode;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private final TransactionRepository transactionRepository;
    private final ChecksumService checksumService;
    private final AuthenticationService authenticationService;
    private final SimulationService simulationService;
    private final CallbackService callbackService;
    private final CallbackBehaviorService callbackBehaviourService;

    public PaymentService(
            TransactionRepository transactionRepository,
            ChecksumService checksumService,
            AuthenticationService authenticationService,
            SimulationService simulationService,
            CallbackService callbackService,
            CallbackBehaviorService callbackBehaviourService) {

        this.transactionRepository = transactionRepository;
        this.checksumService = checksumService;
        this.authenticationService = authenticationService;
        this.simulationService = simulationService;
        this.callbackService = callbackService;
        this.callbackBehaviourService = callbackBehaviourService;
    }

    /**
     * Main payment processing flow.
     */
    public PaymentResponse processPayment(PaymentRequest request) {
    	System.out.println("========== PAYMENT REQUEST ==========");
    	System.out.println("PRN: " + request.getPrn());
    	System.out.println("MD: " + request.getMd());
    	System.out.println("PID: " + request.getPid());
    	System.out.println("NAR: " + request.getNar());
    	System.out.println("AMT: " + request.getAmt());
    	System.out.println("CRN: " + request.getCrn());
    	System.out.println("RU: " + request.getRu());
    	
    	System.out.println("=====================================");
       log.info("========== PAYMENT START ==========");
       log.info("Processing payment for PRN={}", request.getPrn());

        
        validatePaymentRequest(request);

        
        String generatedChecksum =
                checksumService.generatePaymentChecksum(request);

        log.info("Generated checksum = {}", generatedChecksum);
        log.info("Received checksum  = {}", request.getCheckVal());
        
        if (request.getCheckVal() == null || request.getCheckVal().isBlank()) {
            log.error("CheckVal is NULL or EMPTY");
            throw new InvalidChecksumException("Checkval is required");
        }

        if (!generatedChecksum.equalsIgnoreCase(request.getCheckVal())) {

            log.error(
                    "Checksum validation failed for PRN={}",
                    request.getPrn()
            );

            throw new InvalidChecksumException(
                    "Invalid checksum"
            );
        }

        log.info(
                "Checksum validation successful for PRN={}",
                request.getPrn()
        );

        Transaction transaction =
                createTransaction(request);

        transaction.setStatus(
                TransactionStatus.VALIDATED
        );

        transaction.setUpdatedAt(
                LocalDateTime.now()
        );

        
        boolean authenticated =
                authenticationService.authenticate(request);

        if (!authenticated) {

            log.warn(
                    "Authentication failed for PRN={}",
                    request.getPrn()
            );

            transaction.setStatus(
                    TransactionStatus.FAILURE
            );

            transaction.setUpdatedAt(
                    LocalDateTime.now()
            );

            // IMPORTANT:
            // Save transaction even when authentication fails.
            transactionRepository.save(transaction);

            sendCallback(transaction);

            log.info(
                    "Payment completed with FAILURE for PRN={}",
                    request.getPrn()
            );

            return buildPaymentResponse(transaction);
        }

        transaction.setStatus(
                TransactionStatus.AUTHENTICATED
        );

        transaction.setPaymentMode(
                PaymentMode.REAL_TIME
        );

        
        SimulationConfig config =
                simulationService.getConfiguration(
                        request.getPrn()
                );
        
        TransactionStatus finalStatus =
                simulationService.determineStatus(
                        request.getPrn()
                );

        transaction.setStatus(finalStatus);

        transaction.setUpdatedAt(
                LocalDateTime.now()
        );


        transactionRepository.save(transaction);
        if (config != null &&
                config.getSimulationMode() == SimulationMode.DROP) {

            log.info(
                    "PRN={} | DROP scenario | Callback will not be sent",
                    request.getPrn()
            );

            return null;
        }


        log.info(
                "Transaction saved successfully. PRN={}, STATUS={}",
                transaction.getPrn(),
                transaction.getStatus()
        );

        sendCallback(transaction);

        
        log.info(
                "Payment completed. PRN={}, STATUS={}",
                transaction.getPrn(),
                transaction.getStatus()
        );

        log.info("========== PAYMENT END ==========");

        return buildPaymentResponse(transaction);
    }

    
    private void validatePaymentRequest(
            PaymentRequest request) {

        if (request == null) {

            throw new InvalidRequestException(
                    "Payment request cannot be null"
            );
        }

        // MD must be P for payment
        if (!"P".equalsIgnoreCase(request.getMd())) {

            throw new InvalidRequestException(
                    "MD must be P for a payment request"
            );
        }

        // PRN is mandatory
        if (request.getPrn() == null ||
                request.getPrn().isBlank()) {

            throw new InvalidRequestException(
                    "PRN is required"
            );
        }

        // Prevent duplicate payment creation
        if (transactionRepository.existsByPrn(
                request.getPrn())) {

            throw new InvalidRequestException(
                    "Transaction already exists for PRN: "
                            + request.getPrn()
            );
        }

        // Amount validation
        if (request.getAmt() == null ||
                request.getAmt().isBlank()) {

            throw new InvalidRequestException(
                    "Amount is required"
            );
        }

        try {

            BigDecimal amount =
                    new BigDecimal(request.getAmt());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                throw new InvalidRequestException(
                        "Amount must be greater than zero"
                );
            }

        } catch (NumberFormatException exception) {

            throw new InvalidRequestException(
                    "Amount must be a valid numeric value"
            );
        }
    }

   
    private Transaction createTransaction(
            PaymentRequest request) {

        Transaction transaction =
                new Transaction();

        transaction.setPrn(
                request.getPrn()
        );

        transaction.setPayeeId(
                request.getPid()
        );

        transaction.setMerchantName(
                request.getNar()
        );

        transaction.setAmount(
                new BigDecimal(request.getAmt())
        );

        transaction.setCurrency(
                request.getCrn()
        );

        transaction.setAccountNumber(
                request.getAccno()
        );

        transaction.setCallbackUrl(
                request.getRu()
        );

        transaction.setCreatedAt(
                LocalDateTime.now()
        );

        transaction.setUpdatedAt(
                LocalDateTime.now()
        );

        return transaction;
    }

    
    private void sendCallback(
            Transaction transaction) {

        try {

            PaymentCallback callback =
                    callbackService.buildCallback(
                            transaction
                    );

            SimulationConfig config =
                    simulationService.getConfiguration(
                            transaction.getPrn()
                    );

            log.info("========================================");
            log.info("CALLING CALLBACK BEHAVIOR SERVICE");
            log.info("PRN        : {}", transaction.getPrn());
            log.info("CALLBACK URL: {}", transaction.getCallbackUrl());
            log.info("CONFIG     : {}", config);
            log.info("========================================");

            callbackBehaviourService.execute(
                    transaction.getCallbackUrl(),
                    callback,
                    config
            );

        } catch (Exception exception) {

            log.error(
                    "Callback execution failed for PRN={}",
                    transaction.getPrn(),
                    exception
            );
        }
    }

    
    private PaymentResponse buildPaymentResponse(
            Transaction transaction) {

        PaymentResponse response =
                new PaymentResponse();

        response.setStatus(
                mapStatus(transaction.getStatus())
        );

        response.setPrn(
                transaction.getPrn()
        );

        response.setNar(
                transaction.getMerchantName()
        );

        response.setAmt(
                transaction.getAmount() != null
                        ? transaction.getAmount().toPlainString()
                        : null
        );

        response.setAccno(
                transaction.getAccountNumber()
        );

        return response;
    }

    
    private String mapStatus(
            TransactionStatus status) {

        if (status == null) {
            return "P";
        }

        return switch (status) {

            case SUCCESS ->
                    "Y";

            case FAILURE ->
                    "N";

            case PENDING ->
                    "P";

            case VALIDATED,
                 AUTHENTICATED ->
                    "P";

            default ->
                    "P";
        };
    }
}