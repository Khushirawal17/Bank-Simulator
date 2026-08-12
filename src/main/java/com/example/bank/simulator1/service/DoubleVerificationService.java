package com.example.bank.simulator1.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.VerificationRequest;
import com.example.bank.simulator1.dto.VerificationResponse;
import com.example.bank.simulator1.exception.InvalidRequestException;
import com.example.bank.simulator1.exception.TransactionNotFoundException;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.repository.TransactionRepository;
import com.example.bank.simulator1.state.TransactionStatus;
import com.example.bank.simulator1.state.VerificationStatus;

@Service
public class DoubleVerificationService {

    private final TransactionRepository transactionRepository;

    public DoubleVerificationService(
            TransactionRepository transactionRepository) {

        this.transactionRepository =
                transactionRepository;
    }

    public VerificationResponse verify(
            VerificationRequest request) {

        validateRequest(request);

        Transaction transaction =
                transactionRepository
                        .findByPrn(request.getPrn())
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Transaction not found for PRN: "
                                                + request.getPrn()
                                )
                        );

        boolean detailsMatch =
                validateTransactionDetails(
                        transaction,
                        request
                );

        if (!detailsMatch) {

            transaction.setVerificationStatus(
                    VerificationStatus.VERIFICATION_FAILED
            );

            transaction.setErrorMessage(
                    "Verification details do not match payment transaction"
            );

            transaction.setUpdatedAt(
                    LocalDateTime.now()
            );

            transactionRepository.save(transaction);

            return buildFailureResponse(transaction);
        }

        /*
         * Payment must have completed successfully
         * before verification can succeed.
         */
        if (transaction.getStatus()
                != TransactionStatus.SUCCESS) {

            transaction.setVerificationStatus(
                    VerificationStatus.VERIFICATION_FAILED
            );

            transaction.setErrorMessage(
                    "Payment transaction is not successful"
            );

            transaction.setUpdatedAt(
                    LocalDateTime.now()
            );

            transactionRepository.save(transaction);

            return buildFailureResponse(transaction);
        }

        /*
         * Everything matched and payment was successful.
         */
        transaction.setVerificationStatus(
                VerificationStatus.VERIFIED
        );

        transaction.setErrorMessage(null);

        transaction.setUpdatedAt(
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);

        return buildSuccessResponse(transaction);
    }

    private void validateRequest(
            VerificationRequest request) {

        if (request == null) {

            throw new InvalidRequestException(
                    "Verification request cannot be null"
            );
        }

        if (!"V".equalsIgnoreCase(request.getMd())) {

            throw new InvalidRequestException(
                    "MD must be V for a verification request"
            );
        }
    }

    private boolean validateTransactionDetails(
            Transaction transaction,
            VerificationRequest request) {

        /*
         * PRN
         */
        if (!safeEquals(
                transaction.getPrn(),
                request.getPrn())) {

            return false;
        }

        /*
         * PID
         */
        if (!safeEquals(
                transaction.getPayeeId(),
                request.getPid())) {

            return false;
        }

        /*
         * Narration
         */
        if (!safeEquals(
                transaction.getMerchantName(),
                request.getNar())) {

            return false;
        }

        /*
         * Currency
         */
        if (!safeEquals(
                transaction.getCurrency(),
                request.getCrn())) {

            return false;
        }

        /*
         * Amount
         */
        if (transaction.getAmount() == null
                || request.getAmt() == null) {

            return false;
        }

        try {

            BigDecimal requestedAmount =
                    new BigDecimal(request.getAmt());

            if (transaction.getAmount()
                    .compareTo(requestedAmount) != 0) {

                return false;
            }

        } catch (NumberFormatException exception) {

            return false;
        }

        /*
         * Bank Reference ID.
         *
         * Only compare it when both sides provide it.
         */
        if (request.getBid() != null
                && !request.getBid().isBlank()) {

            if (!safeEquals(
                    transaction.getBankReferenceId(),
                    request.getBid())) {

                return false;
            }
        }

        /*
         * Payment Date.
         *
         * Only compare it when the verification request
         * contains a date.
         */
        if (request.getDate() != null
                && !request.getDate().isBlank()) {

            if (!safeEquals(
                    transaction.getPaymentDate(),
                    request.getDate())) {

                return false;
            }
        }

        return true;
    }

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

    private VerificationResponse
    buildSuccessResponse(
            Transaction transaction) {

        VerificationResponse response =
                new VerificationResponse();

        response.setStatus("Y");

        response.setPrn(
                transaction.getPrn()
        );

        if (transaction.getAmount() != null) {

            response.setAmount(
                    transaction.getAmount()
                            .toPlainString()
            );
        }

        response.setMessage(
                "Transaction verification successful"
        );

        return response;
    }

    private VerificationResponse
    buildFailureResponse(
            Transaction transaction) {

        VerificationResponse response =
                new VerificationResponse();

        response.setStatus("N");

        response.setPrn(
                transaction.getPrn()
        );

        if (transaction.getAmount() != null) {

            response.setAmount(
                    transaction.getAmount()
                            .toPlainString()
            );
        }

        response.setMessage(
                transaction.getErrorMessage() != null
                        ? transaction.getErrorMessage()
                        : "Transaction verification failed"
        );

        return response;
    }
}