package com.example.bank.simulator1.service;

import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.SimulationConfig;

@Service
public class CallbackBehaviorService {

    private final BankCallbackClient callbackClient;

    public CallbackBehaviorService(
            BankCallbackClient callbackClient) {

        this.callbackClient = callbackClient;
    }

    public void execute(
            String callbackUrl,
            PaymentCallback callback,
            SimulationConfig config) {

        if (config == null ||
                config.getSimulationMode() == null) {

            callbackClient.sendCallback(
                    callbackUrl,
                    callback
            );

            return;
        }

        switch (config.getSimulationMode()) {

            case DROP -> {
                // Intentionally do not send callback.
            }

            case DELAY -> {
                waitForDelay(config.getDelayMs());

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );
            }

            case DUPLICATE -> {
                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );
            }

            default -> {
                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );
            }
        }
    }

    private void waitForDelay(long delayMs) {

        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Callback delay was interrupted",
                    exception
            );
        }
    }
}