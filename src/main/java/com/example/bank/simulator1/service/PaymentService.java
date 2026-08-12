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
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	private final TransactionRepository transactionRepository;
	private final ChecksumService checksumService;
	private final AuthenticationService authenticationService;
	private final SimulationService simulationService;
	private final CallbackService callbackService;
	private final CallbackBehaviorService callbackBehaviourService;
	private final DoubleVerificationService doubleVerificationService;

	public PaymentService(TransactionRepository transactionRepository, ChecksumService checksumService,
			AuthenticationService authenticationService, SimulationService simulationService,
			CallbackService callbackService, CallbackBehaviorService callbackBehaviourService, DoubleVerificationService doubleVerificationService) {
		this.transactionRepository = transactionRepository;
		this.checksumService = checksumService;
		this.authenticationService = authenticationService;
		this.simulationService = simulationService;
		this.callbackService = callbackService;
		this.callbackBehaviourService = callbackBehaviourService;
		this.doubleVerificationService = doubleVerificationService;
	}

	public PaymentResponse processPayment(PaymentRequest request) {

		validatePaymentRequest(request);

		String generatedChecksum = checksumService.generatePaymentChecksum(request);
		
		System.out.println("Generated checksum = " + generatedChecksum);
		System.out.println("Received checksum  = " + request.getCheckVal());

		if (!generatedChecksum.equalsIgnoreCase(request.getCheckVal())) {
			throw new InvalidChecksumException("Invalid checksum");
		}

		Transaction transaction = createTransaction(request);
		System.out.println("========== PAYMENT DEBUG ==========");
		System.out.println("PRN received: " + request.getPrn());
		System.out.println("PRN created: " + transaction.getPrn());
		System.out.println("===================================");

		transaction.setStatus(TransactionStatus.VALIDATED);

		if (!authenticationService.authenticate(request)) {
			transaction.setStatus(TransactionStatus.FAILURE);
			transaction.setUpdatedAt(LocalDateTime.now());

			transactionRepository.save(transaction);
			System.out.println("========== SAVING TRANSACTION ==========");
			System.out.println("PRN being saved: " + transaction.getPrn());
			System.out.println("Status: " + transaction.getStatus());
			System.out.println("========================================");

			PaymentCallback callback = callbackService.buildCallback(transaction);

			SimulationConfig config = simulationService.getConfiguration(request.getPrn());

			callbackBehaviourService.execute(transaction.getCallbackUrl(), callback, config);

			return buildPaymentResponse(transaction);
		}

		transaction.setStatus(TransactionStatus.AUTHENTICATED);

		transaction.setPaymentMode(PaymentMode.REAL_TIME);

		/*
		 * Determine the result of the simulation.
		 */
		TransactionStatus simulationStatus =
		        simulationService.determineStatus(request.getPrn());

		transaction.setStatus(simulationStatus);

		/*
		 * Only perform double verification when the
		 * simulated transaction is successful.
		 */
		if (simulationStatus == TransactionStatus.SUCCESS) {

		    log.info(
		            "Starting double verification. PRN={}",
		            transaction.getPrn()
		    );

		    boolean doubleVerificationPassed =
		            doubleVerificationService.verify(
		                    transaction.getPrn()
		            );

		    if (!doubleVerificationPassed) {

		        log.warn(
		                "Double verification failed. PRN={}",
		                transaction.getPrn()
		        );

		        transaction.setStatus(
		                TransactionStatus.FAILURE
		        );

		    } else {

		        log.info(
		                "Double verification successful. PRN={}",
		                transaction.getPrn()
		        );
		    }
		}

		transaction.setUpdatedAt(LocalDateTime.now());

		transactionRepository.save(transaction);

		PaymentCallback callback =
		        callbackService.buildCallback(transaction);

		SimulationConfig config =
		        simulationService.getConfiguration(
		                request.getPrn()
		        );

		callbackBehaviourService.execute(
		        transaction.getCallbackUrl(),
		        callback,
		        config
		);

		log.info(
		        "Payment completed. PRN={}, status={}",
		        transaction.getPrn(),
		        transaction.getStatus()
		);

		return buildPaymentResponse(transaction);
	}

	private void validatePaymentRequest(PaymentRequest request) {

		if (!"P".equalsIgnoreCase(request.getMd())) {
			throw new InvalidRequestException("MD must be P for a payment request");
		}

		if (transactionRepository.existsByPrn(request.getPrn())) {
			throw new InvalidRequestException("Transaction already exists for PRN: " + request.getPrn());
		}

		try {
			BigDecimal amount = new BigDecimal(request.getAmt());

			if (amount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new InvalidRequestException("Amount must be greater than zero");
			}

		} catch (NumberFormatException exception) {
			throw new InvalidRequestException("Amount must be a valid numeric value");
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