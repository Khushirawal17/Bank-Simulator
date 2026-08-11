package com.example.bank.simulator1.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.config.BankProperties;
import com.example.bank.simulator1.dto.PaymentRequest;

import static org.junit.jupiter.api.Assertions.*;

class ChecksumServiceTest {

	private ChecksumService checksumService;

	@BeforeEach
	void setUp() {

		BankProperties properties = new BankProperties();

		BankProperties.Security security = new BankProperties.Security();

		security.setChecksumKey("TEST_CHECKSUM_KEY");

		properties.setSecurity(security);

		checksumService = new ChecksumService(properties);
	}

	@Test
	void shouldGenerateConsistentChecksum() {

		PaymentRequest request = createPaymentRequest();

		String checksum1 = checksumService.generatePaymentChecksum(request);

		String checksum2 = checksumService.generatePaymentChecksum(request);

		assertEquals(checksum1, checksum2);
	}

	@Test
	void shouldGenerateDifferentChecksumWhenAmountChanges() {

		PaymentRequest request = createPaymentRequest();

		String originalChecksum = checksumService.generatePaymentChecksum(request);

		request.setAmt("200.00");

		String changedChecksum = checksumService.generatePaymentChecksum(request);

		assertNotEquals(originalChecksum, changedChecksum);
	}

	@Test
	void shouldValidateCorrectChecksum() {

		PaymentRequest request = createPaymentRequest();

		String checksum = checksumService.generatePaymentChecksum(request);

		request.setCheckVal(checksum);

		assertTrue(checksumService.validatePaymentRequest(request));
	}

	@Test
	void shouldRejectIncorrectChecksum() {

		PaymentRequest request = createPaymentRequest();

		request.setCheckVal("INVALID_CHECKSUM");

		assertFalse(checksumService.validatePaymentRequest(request));
	}

	private PaymentRequest createPaymentRequest() {

		PaymentRequest request = new PaymentRequest();

		request.setMd("P");
		request.setPid("TEST001");
		request.setNar("TESTMERCHANT");
		request.setPrn("TXN1001");
		request.setAmt("100.00");
		request.setCrn("INR");
		request.setRu("http://localhost:8081/callback");

		return request;
	}
}