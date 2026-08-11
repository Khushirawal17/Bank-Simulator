package com.example.bank.simulator1.exception;

public class InvalidRequestException extends RuntimeException{

	public InvalidRequestException(String message) {
		super(message);
	}
}
