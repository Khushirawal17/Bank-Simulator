package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.exception.InvalidChecksumException;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.exception.TransactionNotFoundException;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.security.ChecksumService;
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class VerificationService {

	private final TransactionRepository transactionRepository;
	private final ChecksumService checksumService;

	public VerificationService(TransactionRepository transactionRepository, ChecksumService checksumService) {

		this.transactionRepository = transactionRepository;
		this.checksumService = checksumService;
	}

	public VerificationResponse verify(VerificationRequest request) {

		System.out.println();
		System.out.println("==========================================");
		System.out.println("       DOUBLE VERIFICATION START");
		System.out.println("==========================================");

		validateRequest(request);

		boolean checksumValid = checksumService.validateVerificationRequest(request);

		if (!checksumValid) {

			System.out.println("❌ Verification checksum validation FAILED");

			throw new InvalidChecksumException("Invalid verification checksum");
		}

		System.out.println("✅ Verification checksum validated successfully.");

		Transaction transaction = transactionRepository.findByPrn(request.getPrn()).orElseThrow(
				() -> new TransactionNotFoundException("Transaction not found for PRN: " + request.getPrn()));

		System.out.println();
		System.out.println("Original transaction found.");
		System.out.println("PRN = " + transaction.getPrn());

		boolean matches = matchesTransaction(request, transaction);

		if (!matches) {

			VerificationResponse response = new VerificationResponse();

			response.setStatus("N");
			response.setPrn(transaction.getPrn());

			if (transaction.getAmount() != null) {

				response.setAmount(transaction.getAmount().toPlainString());
			}

			response.setMessage("Verification details do not match payment transaction");

			System.out.println();
			System.out.println("❌ DOUBLE VERIFICATION FAILED");

			System.out.println("==========================================");

			return response;
		}

		VerificationResponse response = buildResponse(transaction);

		System.out.println();
		System.out.println("✅ DOUBLE VERIFICATION SUCCESSFUL");

		System.out.println("Verification Status = " + response.getStatus());

		System.out.println("Verification Message = " + response.getMessage());

		System.out.println("==========================================");

		return response;
	}

	private void validateRequest(VerificationRequest request) {

		if (request == null) {

			throw new InvalidRequestException("Verification request cannot be null");
		}

		if (!"V".equalsIgnoreCase(request.getMd())) {

			throw new InvalidRequestException("MD must be V for a verification request");
		}
	}

	private boolean matchesTransaction(VerificationRequest request, Transaction transaction) {

		System.out.println();
		System.out.println("========== VERIFICATION COMPARISON ==========");

		System.out.println("PID:");
		System.out.println("Request     = " + request.getPid());
		System.out.println("Transaction = " + transaction.getPayeeId());

		if (!safeEquals(request.getPid(), transaction.getPayeeId())) {

			System.out.println("❌ PID MISMATCH");

			return false;
		}

		System.out.println("✅ PID MATCH");

		System.out.println();
		System.out.println("Amount:");

		System.out.println("Request     = " + request.getAmt());

		System.out.println("Transaction = " + transaction.getAmount());

		if (transaction.getAmount() == null || request.getAmt() == null) {

			System.out.println("❌ AMOUNT IS NULL");

			return false;
		}

		try {

			if (transaction.getAmount().compareTo(new java.math.BigDecimal(request.getAmt())) != 0) {

				System.out.println("❌ AMOUNT MISMATCH");

				return false;
			}

		} catch (NumberFormatException exception) {

			System.out.println("❌ INVALID AMOUNT");

			return false;
		}

		System.out.println("✅ AMOUNT MATCH");

		System.out.println();
		System.out.println("Narration:");

		System.out.println("Request     = " + request.getNar());

		System.out.println("Transaction = " + transaction.getMerchantName());

		if (!safeEquals(request.getNar(), transaction.getMerchantName())) {

			System.out.println("❌ NARRATION MISMATCH");

			return false;
		}

		System.out.println("✅ NARRATION MATCH");

		System.out.println();
		System.out.println("Currency:");

		System.out.println("Request     = " + request.getCrn());

		System.out.println("Transaction = " + transaction.getCurrency());

		if (!safeEquals(request.getCrn(), transaction.getCurrency())) {

			System.out.println("❌ CURRENCY MISMATCH");

			return false;
		}

		System.out.println("✅ CURRENCY MATCH");

		System.out.println();
		System.out.println("✅ ALL VERIFICATION VALUES MATCH");

		System.out.println("=============================================");

		return true;
	}

	private VerificationResponse buildResponse(Transaction transaction) {

		VerificationResponse response = new VerificationResponse();

		response.setStatus(mapStatus(transaction.getStatus()));

		response.setPrn(transaction.getPrn());

		if (transaction.getAmount() != null) {

			response.setAmount(transaction.getAmount().toPlainString());
		}

		response.setMessage(getStatusMessage(transaction.getStatus()));

		return response;
	}

	private String mapStatus(TransactionStatus status) {

		if (status == null) {
			return "P";
		}

		return switch (status) {

		case SUCCESS -> "Y";

		case FAILURE -> "N";

		case PENDING -> "P";

		default -> "P";
		};
	}

	private String getStatusMessage(TransactionStatus status) {

		if (status == null) {

			return "Transaction is being processed";
		}

		return switch (status) {

		case SUCCESS -> "Transaction successful";

		case FAILURE -> "Transaction failed";

		case PENDING -> "Transaction pending";

		default -> "Transaction is being processed";
		};
	}

	private boolean safeEquals(String first, String second) {

		if (first == null && second == null) {
			return true;
		}

		if (first == null || second == null) {
			return false;
		}

		return first.equals(second);
	}
}