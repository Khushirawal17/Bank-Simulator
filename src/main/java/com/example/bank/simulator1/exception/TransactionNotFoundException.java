package com.example.bank.simulator1.exception;

public class TransactionNotFoundException extends RuntimeException {

	public TransactionNotFoundException(String message) {
		super(message);
	}
}
