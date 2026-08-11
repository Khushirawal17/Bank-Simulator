package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.state.TransactionStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CallbackService {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private final BankReferenceService bankReferenceService;

	public CallbackService(BankReferenceService bankReferenceService) {

		this.bankReferenceService = bankReferenceService;
	}

	public PaymentCallback buildCallback(Transaction transaction) {

		PaymentCallback callback = this.buildCallback(transaction);

		callback.setStatus(mapStatus(transaction.getStatus()));

		callback.setPrn(transaction.getPrn());

		callback.setNar(transaction.getMerchantName());

		callback.setBid(bankReferenceService.generate());

		callback.setAmt(transaction.getAmount().toPlainString());

		callback.setAccno(transaction.getAccountNumber());

		callback.setDate(LocalDateTime.now().format(DATE_FORMAT));

		callback.setErrormsg(getErrorMessage(transaction.getStatus()));

		return callback;
	}

	private String mapStatus(TransactionStatus status) {

		return switch (status) {
		case SUCCESS -> "Y";
		case FAILURE -> "N";
		case PENDING -> "P";
		default -> "N";
		};
	}

	private String getErrorMessage(TransactionStatus status) {

		return switch (status) {
		case FAILURE -> "Transaction failed";

		case SUCCESS, PENDING -> "";

		default -> "Transaction status unavailable";
		};
	}
}