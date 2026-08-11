package com.example.bank.simulator1.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentRequest;
import com.example.bank.simulator1.dto.PaymentResponse;
import com.example.bank.simulator1.exception.InvalidChecksumException;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.security.ChecksumService;
import com.example.bank.simulator1.state.PaymentMode;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class PaymentService {
	
	private final TransactionRepository transactionRepository;
	private final ChecksumService checksumService;
	private final AuthenticationService authenticationService;
	private final SimulationService simulationService;
	private final CallbackService callbackService;
	private final CallbackBehaviorService callbackBehaviourService;
	
	public PaymentService(TransactionRepository transactionRepository,ChecksumService checksumService,AuthenticationService authenticationService, SimulationService simulationService, CallbackService callbackService, CallbackBehaviorService callbackBehaviourService) {
		this.transactionRepository = transactionRepository;
		this.checksumService = checksumService;
		this.authenticationService = authenticationService;
		this.simulationService = simulationService;
		this.callbackService = callbackService;
		this.callbackBehaviourService = callbackBehaviourService;
	}
	
	public PaymentResponse processPayment(PaymentRequest request) {
		
		validatePaymentRequest(request);
		if (!checksumService.validatePaymentRequest(request)) {
	        throw new InvalidChecksumException(
	                "Invalid checksum"
	        );
	    }
		
		Transaction transaction = createTransaction(request);
				
		transaction.setStatus(TransactionStatus.VALIDATED);
		
		if (!authenticationService.authenticate(request)) {
	        transaction.setStatus(TransactionStatus.FAILURE);
	        transaction.setUpdatedAt(LocalDateTime.now());

	        transactionRepository.save(transaction);

	        return buildPaymentResponse(transaction);
	    }

        transaction.setStatus(TransactionStatus.AUTHENTICATED);

        transaction.setPaymentMode(PaymentMode.REAL_TIME);

        transaction.setStatus(
        		simulationService.determineStatus(
        				request.getPrn())
        		);

        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return buildPaymentResponse(transaction);
    }
	
	private void validatePaymentRequest(PaymentRequest request) {

	    if (!"P".equalsIgnoreCase(request.getMd())) {
	        throw new InvalidRequestException(
	                "MD must be P for a payment request"
	        );
	    }

	    if (transactionRepository.existsByPrn(request.getPrn())) {
	        throw new InvalidRequestException(
	                "Transaction already exists for PRN: " + request.getPrn()
	        );
	    }

	    try {
	        BigDecimal amount = new BigDecimal(request.getAmt());

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

    private Transaction createTransaction(PaymentRequest request) {

        Transaction transaction = new Transaction();

        transaction.setPrn(request.getPrn());
        transaction.setPayeeId(request.getPid());
        transaction.setMerchantName(request.getNar());
        transaction.setAmount(new BigDecimal(request.getAmt()));
        transaction.setCurrency(request.getCrn());
        transaction.setAccountNumber(request.getAccno());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCallbackUrl(request.getRu());

        return transaction;
    }

    private PaymentResponse buildPaymentResponse(Transaction transaction) {

        PaymentResponse response = new PaymentResponse();

        response.setStatus(mapStatus(transaction.getStatus()));
        response.setPrn(transaction.getPrn());
        response.setNar(transaction.getMerchantName());
        response.setAmt(transaction.getAmount().toPlainString());
        response.setAccno(transaction.getAccountNumber());

        return response;
    }

    private String mapStatus(TransactionStatus status) {

        return switch (status) {
            case SUCCESS -> "Y";
            case FAILURE -> "N";
            case PENDING -> "P";
            default -> "N";
        };
	}
	
}