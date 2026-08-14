package com.example.bank.simulator1.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.state.SimulationMode;

@ExtendWith(MockitoExtension.class)
class CallbackBehaviorServiceTest {

	@Mock
	private BankCallbackClient callbackClient;

	private CallbackBehaviorService callbackBehaviorService;

	private PaymentCallback callback;

	private final String callbackUrl = "http://localhost:9090/bank/callback";

	@BeforeEach
	void setUp() {

		callbackBehaviorService = new CallbackBehaviorService(callbackClient);

		callback = new PaymentCallback();

		callback.setPrn("PRN001");
		callback.setStatus("Y");
		callback.setPid("TEST001");
		callback.setNar("Test Payment");
		callback.setAmt("100.00");
	}

	// =====================================================
	// TEST 1: SUCCESS
	// =====================================================

	@Test
	void shouldSendCallbackForSuccess() {

		SimulationConfig config = new SimulationConfig("PRN001", SimulationMode.SUCCESS, 0);

		callbackBehaviorService.execute(callbackUrl, callback, config);

		verify(callbackClient, times(1)).sendCallback(callbackUrl, callback);
	}

	// =====================================================
	// TEST 2: FAILURE
	// =====================================================

	@Test
	void shouldSendCallbackForFailure() {

		SimulationConfig config = new SimulationConfig("PRN001", SimulationMode.FAILURE, 0);

		callbackBehaviorService.execute(callbackUrl, callback, config);

		verify(callbackClient, times(1)).sendCallback(callbackUrl, callback);
	}

	// =====================================================
	// TEST 3: PENDING
	// =====================================================

	@Test
	void shouldSendCallbackForPending() {

		SimulationConfig config = new SimulationConfig("PRN001", SimulationMode.PENDING, 0);

		callbackBehaviorService.execute(callbackUrl, callback, config);

		verify(callbackClient, times(1)).sendCallback(callbackUrl, callback);
	}

	// =====================================================
	// TEST 4: DELAY
	// =====================================================

	@Test
	void shouldSendCallbackAfterDelay() {

		SimulationConfig config = new SimulationConfig("PRN001", SimulationMode.DELAY, 10);

		assertDoesNotThrow(() -> callbackBehaviorService.execute(callbackUrl, callback, config));

		verify(callbackClient, times(1)).sendCallback(callbackUrl, callback);
	}

	// =====================================================
	// TEST 5: DROP
	// =====================================================

	@Test
	void shouldNotSendCallbackForDrop() {

		SimulationConfig config = new SimulationConfig("PRN001", SimulationMode.DROP, 0);

		callbackBehaviorService.execute(callbackUrl, callback, config);

		verify(callbackClient, never()).sendCallback(anyString(), any(PaymentCallback.class));
	}

	// =====================================================
	// TEST 6: DUPLICATE
	// =====================================================

	@Test
	void shouldSendCallbackTwiceForDuplicate() {

		SimulationConfig config = new SimulationConfig("PRN001", SimulationMode.DUPLICATE, 0);

		callbackBehaviorService.execute(callbackUrl, callback, config);

		verify(callbackClient, times(2)).sendCallback(callbackUrl, callback);
	}

	// =====================================================
	// TEST 7: NULL CONFIG
	// =====================================================

	@Test
	void shouldUseSuccessWhenConfigIsNull() {

		callbackBehaviorService.execute(callbackUrl, callback, null);

		verify(callbackClient, times(1)).sendCallback(callbackUrl, callback);
	}
}