package com.example.bank.simulator1.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.bank.simulator1.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException exception) {

		ErrorResponse response = new ErrorResponse("INVALID_REQUEST", exception.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException exception) {

		String message = exception.getBindingResult().getFieldErrors().stream().findFirst()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).orElse("Invalid request");

		ErrorResponse response = new ErrorResponse("VALIDATION_ERROR", message);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTransactionNotFound(TransactionNotFoundException exception) {

		ErrorResponse response = new ErrorResponse("TRANSACTION_NOT_FOUND", exception.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {

	    ErrorResponse response = new ErrorResponse(
	            "INTERNAL_ERROR",
	            exception.getMessage(),
	            LocalDateTime.now()
	    );

	    return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(response);
	}
	
	 @ExceptionHandler(InvalidChecksumException.class)
	 public ResponseEntity<ErrorResponse> handleInvalidChecksum(
	         InvalidChecksumException exception) {

	     ErrorResponse response = new ErrorResponse(
	             "INVALID_CHECKSUM",
	             exception.getMessage()
	     );

	     return ResponseEntity
	             .status(HttpStatus.BAD_REQUEST)
	             .body(response);
	 }
}