package com.example.bank.simulator1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.Transaction;
import com.example.bank.simulator1.state.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class CallbackServiceTest {

	@Mock
	private BankReferenceService bankReferenceService;

	private CallbackService callbackService;

	@BeforeEach
	void setUp() {

		callbackService = new CallbackService(bankReferenceService);
	}

	// =====================================================
	// TEST 1: SUCCESS callback
	// =====================================================

	@Test
	void shouldBuildSuccessCallback() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		when(bankReferenceService.generate()).thenReturn("BID001");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertNotNull(callback);

		assertEquals("Y", callback.getStatus());
		assertEquals("PRN001", callback.getPrn());

		// PID
		assertEquals("TEST001", callback.getPid());

		assertEquals("Test Payment", callback.getNar());

		assertEquals("BID001", callback.getBid());

		assertEquals("100.00", callback.getAmt());

		assertEquals("1234567890", callback.getAccno());

		assertEquals("", callback.getErrormsg());

		// Date should be generated
		assertNotNull(callback.getDate());

		verify(bankReferenceService).generate();
	}

	// =====================================================
	// TEST 2: FAILURE callback
	// =====================================================

	@Test
	void shouldBuildFailureCallback() {

		Transaction transaction = createTransaction(TransactionStatus.FAILURE);

		when(bankReferenceService.generate()).thenReturn("BID002");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertNotNull(callback);

		assertEquals("N", callback.getStatus());

		assertEquals("PRN001", callback.getPrn());

		assertEquals("TEST001", callback.getPid());

		assertEquals("BID002", callback.getBid());

		assertEquals("Transaction failed", callback.getErrormsg());
	}

	// =====================================================
	// TEST 3: PENDING callback
	// =====================================================

	@Test
	void shouldBuildPendingCallback() {

		Transaction transaction = createTransaction(TransactionStatus.PENDING);

		when(bankReferenceService.generate()).thenReturn("BID003");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertNotNull(callback);

		assertEquals("P", callback.getStatus());

		assertEquals("PRN001", callback.getPrn());

		assertEquals("TEST001", callback.getPid());

		assertEquals("BID003", callback.getBid());

		assertEquals("", callback.getErrormsg());
	}

	// =====================================================
	// TEST 4: Amount should be correct
	// =====================================================

	@Test
	void shouldSetCorrectAmount() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		transaction.setAmount(new BigDecimal("250.50"));

		when(bankReferenceService.generate()).thenReturn("BID004");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertEquals("250.50", callback.getAmt());
	}

	// =====================================================
	// TEST 5: PRN should be copied
	// =====================================================

	@Test
	void shouldSetCorrectPrn() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		transaction.setPrn("PRN999");

		when(bankReferenceService.generate()).thenReturn("BID005");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertEquals("PRN999", callback.getPrn());
	}

	// =====================================================
	// TEST 6: PID should be copied
	// =====================================================

	@Test
	void shouldSetCorrectPid() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		transaction.setPayeeId("MERCHANT999");

		when(bankReferenceService.generate()).thenReturn("BID006");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertEquals("MERCHANT999", callback.getPid());
	}

	// =====================================================
	// TEST 7: Merchant name should become narration
	// =====================================================

	@Test
	void shouldSetMerchantNameAsNarration() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		transaction.setMerchantName("Laptop Purchase");

		when(bankReferenceService.generate()).thenReturn("BID007");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertEquals("Laptop Purchase", callback.getNar());
	}

	// =====================================================
	// TEST 8: Bank reference should be generated
	// =====================================================

	@Test
	void shouldGenerateBankReference() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		when(bankReferenceService.generate()).thenReturn("BANK-REF-123");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertEquals("BANK-REF-123", callback.getBid());

		verify(bankReferenceService, times(1)).generate();
	}

	// =====================================================
	// TEST 9: Account number should be copied
	// =====================================================

	@Test
	void shouldSetCorrectAccountNumber() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		transaction.setAccountNumber("9876543210");

		when(bankReferenceService.generate()).thenReturn("BID009");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertEquals("9876543210", callback.getAccno());
	}

	// =====================================================
	// TEST 10: Date should be generated
	// =====================================================

	@Test
	void shouldGenerateCallbackDate() {

		Transaction transaction = createTransaction(TransactionStatus.SUCCESS);

		when(bankReferenceService.generate()).thenReturn("BID010");

		PaymentCallback callback = callbackService.buildCallback(transaction);

		assertNotNull(callback.getDate());

		assertFalse(callback.getDate().isBlank());
	}

	// =====================================================
	// HELPER METHOD
	// =====================================================

	private Transaction createTransaction(TransactionStatus status) {

		Transaction transaction = new Transaction();

		transaction.setPrn("PRN001");

		transaction.setPayeeId("TEST001");

		transaction.setMerchantName("Test Payment");

		transaction.setAmount(new BigDecimal("100.00"));

		transaction.setAccountNumber("1234567890");

		transaction.setCurrency("INR");

		transaction.setStatus(status);

		return transaction;
	}
}