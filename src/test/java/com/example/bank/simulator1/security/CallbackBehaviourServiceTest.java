package com.example.bank.simulator1.security;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.service.BankCallbackClient;
import com.example.bank.simulator1.service.CallbackBehaviorService;
import com.example.bank.simulator1.state.SimulationMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CallbackBehaviorServiceTest {

    private BankCallbackClient callbackClient;
    private CallbackBehaviorService behaviorService;

    @BeforeEach
    void setUp() {

        callbackClient =
                mock(BankCallbackClient.class);

        behaviorService =
                new CallbackBehaviorService(
                        callbackClient
                );
    }

    @Test
    void shouldSendCallbackNormally() {

        PaymentCallback callback =
                createCallback();

        behaviorService.execute(
                "http://localhost:9999/callback",
                callback,
                null
        );

        verify(callbackClient, times(1))
                .sendCallback(
                        "http://localhost:9999/callback",
                        callback
                );
    }

    @Test
    void shouldDropCallback() {

        PaymentCallback callback =
                createCallback();

        SimulationConfig config =
                new SimulationConfig(
                        "TXN1001",
                        SimulationMode.DROP,
                        0
                );

        behaviorService.execute(
                "http://localhost:9999/callback",
                callback,
                config
        );

        verify(
                callbackClient,
                never()
        ).sendCallback(
                anyString(),
                any()
        );
    }

    @Test
    void shouldSendDuplicateCallback() {

        PaymentCallback callback =
                createCallback();

        SimulationConfig config =
                new SimulationConfig(
                        "TXN1001",
                        SimulationMode.DUPLICATE,
                        0
                );

        behaviorService.execute(
                "http://localhost:9999/callback",
                callback,
                config
        );

        verify(
                callbackClient,
                times(2)
        ).sendCallback(
                "http://localhost:9999/callback",
                callback
        );
    }

    @Test
    void shouldSendDelayedCallback() {

        PaymentCallback callback =
                createCallback();

        SimulationConfig config =
                new SimulationConfig(
                        "TXN1001",
                        SimulationMode.DELAY,
                        100
                );

        long start = System.currentTimeMillis();

        behaviorService.execute(
                "http://localhost:9999/callback",
                callback,
                config
        );

        long elapsed =
                System.currentTimeMillis() - start;

        assertTrue(elapsed >= 100);

        verify(
                callbackClient,
                times(1)
        ).sendCallback(
                "http://localhost:9999/callback",
                callback
        );
    }

    private PaymentCallback createCallback() {

        PaymentCallback callback =
                new PaymentCallback();

        callback.setPrn("TXN1001");
        callback.setStatus("Y");

        return callback;
    }
}