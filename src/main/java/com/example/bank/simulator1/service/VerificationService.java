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

    public VerificationService(
            TransactionRepository transactionRepository,
            ChecksumService checksumService) {

        this.transactionRepository = transactionRepository;
        this.checksumService = checksumService;
    }

    public VerificationResponse verify(
            VerificationRequest request) {

        System.out.println(
                "========== DOUBLE VERIFICATION START =========="
        );

        validateRequest(request);

        // -------------------------------------------------
        // STEP 1: Validate verification checksum
        // -------------------------------------------------

        if (request.getCheckVal() == null ||
                request.getCheckVal().isBlank()) {

            throw new InvalidChecksumException(
                    "Verification checksum is required"
            );
        }

        boolean checksumValid =
                checksumService.validateVerificationRequest(
                        request
                );

        if (!checksumValid) {

            throw new InvalidChecksumException(
                    "Invalid verification checksum"
            );
        }

        System.out.println(
                "Verification checksum validated successfully."
        );

        // -------------------------------------------------
        // STEP 2: Find original payment transaction
        // -------------------------------------------------

        Transaction transaction =
                transactionRepository
                        .findByPrn(request.getPrn())
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Transaction not found for PRN: "
                                                + request.getPrn()
                                )
                        );

        System.out.println(
                "Original transaction found. PRN="
                        + transaction.getPrn()
        );

        // -------------------------------------------------
        // STEP 3: Compare verification data
        // -------------------------------------------------

        boolean matches =
                matchesTransaction(
                        request,
                        transaction
                );

        if (!matches) {

            VerificationResponse response =
                    new VerificationResponse();

            response.setStatus("N");
            response.setPrn(transaction.getPrn());

            if (transaction.getAmount() != null) {

                response.setAmount(
                        transaction
                                .getAmount()
                                .toPlainString()
                );
            }

            response.setMessage(
                    "Verification details do not match payment transaction"
            );

            System.out.println(
                    "DOUBLE VERIFICATION FAILED. PRN="
                            + transaction.getPrn()
            );

            System.out.println(
                    "========== DOUBLE VERIFICATION END =========="
            );

            return response;
        }

        // -------------------------------------------------
        // STEP 4: Return transaction status
        // -------------------------------------------------

        VerificationResponse response =
                buildResponse(transaction);

        System.out.println(
                "DOUBLE VERIFICATION SUCCESSFUL. PRN="
                        + transaction.getPrn()
        );

        System.out.println(
                "========== DOUBLE VERIFICATION END =========="
        );

        return response;
    }

    // =====================================================
    // REQUEST VALIDATION
    // =====================================================

    private void validateRequest(
            VerificationRequest request) {

        if (!"V".equalsIgnoreCase(request.getMd())) {

            throw new InvalidRequestException(
                    "MD must be V for a verification request"
            );
        }
    }

    // =====================================================
    // TRANSACTION COMPARISON
    // =====================================================

    private boolean matchesTransaction(
            VerificationRequest request,
            Transaction transaction) {

        // PID
        if (!safeEquals(
                request.getPid(),
                transaction.getPayeeId())) {

            return false;
        }

        // Amount
        if (transaction.getAmount() == null ||
                request.getAmt() == null) {

            return false;
        }

        try {

            if (transaction.getAmount()
                    .compareTo(
                            new java.math.BigDecimal(
                                    request.getAmt()
                            )
                    ) != 0) {

                return false;
            }

        } catch (NumberFormatException exception) {

            return false;
        }

        // Narration
        if (!safeEquals(
                request.getNar(),
                transaction.getMerchantName())) {

            return false;
        }

        // Currency
        if (!safeEquals(
                request.getCrn(),
                transaction.getCurrency())) {

            return false;
        }

        return true;
    }

    // =====================================================
    // BUILD SUCCESS RESPONSE
    // =====================================================

    private VerificationResponse buildResponse(
            Transaction transaction) {

        VerificationResponse response =
                new VerificationResponse();

        response.setStatus(
                mapStatus(
                        transaction.getStatus()
                )
        );

        response.setPrn(
                transaction.getPrn()
        );

        if (transaction.getAmount() != null) {

            response.setAmount(
                    transaction
                            .getAmount()
                            .toPlainString()
            );
        }

        response.setMessage(
                getStatusMessage(
                        transaction.getStatus()
                )
        );

        return response;
    }

    // =====================================================
    // STATUS MAPPING
    // =====================================================

    private String mapStatus(
            TransactionStatus status) {

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

    // =====================================================
    // STATUS MESSAGE
    // =====================================================

    private String getStatusMessage(
            TransactionStatus status) {

        if (status == null) {
            return "Transaction is being processed";
        }

        return switch (status) {

            case SUCCESS ->
                    "Transaction successful";

            case FAILURE ->
                    "Transaction failed";

            case PENDING ->
                    "Transaction pending";

            default ->
                    "Transaction is being processed";
        };
    }

    // =====================================================
    // SAFE STRING COMPARISON
    // =====================================================

    private boolean safeEquals(
            String first,
            String second) {

        if (first == null && second == null) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        return first.equals(second);
    }
}