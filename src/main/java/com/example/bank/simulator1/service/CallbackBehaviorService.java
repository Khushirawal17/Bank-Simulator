package com.example.bank.simulator1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.bank.simulator1.dto.PaymentCallback;
import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.state.SimulationMode;

@Service
public class CallbackBehaviorService {

    private static final Logger log =
            LoggerFactory.getLogger(CallbackBehaviorService.class);

    private final BankCallbackClient callbackClient;

    public CallbackBehaviorService(
            BankCallbackClient callbackClient) {

        this.callbackClient = callbackClient;
    }

    public void execute(
            String callbackUrl,
            PaymentCallback callback,
            SimulationConfig config) {

        String prn = callback.getPrn();

        SimulationMode mode =
                config != null
                        ? config.getSimulationMode()
                        : SimulationMode.SUCCESS;

        log.info("");
        log.info("========== CALLBACK BEHAVIOR ==========");
        log.info("PRN       : {}", prn);
        log.info("MODE      : {}", mode);
        log.info("=======================================");

        switch (mode) {

            case SUCCESS -> {

                log.info(
                        "PRN={} | SUCCESS scenario | Sending callback",
                        prn
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                log.info(
                        "PRN={} | Callback sent successfully",
                        prn
                );
            }

            case FAILURE -> {

                log.info(
                        "PRN={} | FAILURE scenario | Sending failure callback",
                        prn
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                log.info(
                        "PRN={} | Failure callback sent",
                        prn
                );
            }

            case PENDING -> {

                log.info(
                        "PRN={} | PENDING scenario | Sending pending callback",
                        prn
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                log.info(
                        "PRN={} | Pending callback sent",
                        prn
                );
            }

            case DELAY -> {

                long delayMs =
                        config != null
                                ? config.getDelayMs()
                                : 0;

                log.info(
                        "PRN={} | DELAY scenario",
                        prn
                );

                log.info(
                        "PRN={} | Waiting {} ms before callback",
                        prn,
                        delayMs
                );

                waitForDelay(delayMs);

                log.info(
                        "PRN={} | Delay completed | Sending callback",
                        prn
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                log.info(
                        "PRN={} | Delayed callback sent successfully",
                        prn
                );
            }

            case DROP -> {

                log.warn(
                        "PRN={} | DROP scenario | Callback will NOT be sent",
                        prn
                );

                log.warn(
                        "PRN={} | Callback intentionally dropped",
                        prn
                );
            }

            case DUPLICATE -> {

                log.warn(
                        "PRN={} | DUPLICATE scenario",
                        prn
                );

                log.info(
                        "PRN={} | Sending callback #1",
                        prn
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                log.info(
                        "PRN={} | Callback #1 sent successfully",
                        prn
                );

                log.info(
                        "PRN={} | Sending callback #2",
                        prn
                );

                callbackClient.sendCallback(
                        callbackUrl,
                        callback
                );

                log.info(
                        "PRN={} | Callback #2 sent successfully",
                        prn
                );
            }
        }

        log.info(
                "========== CALLBACK END | PRN={} ==========",
                prn
        );
        log.info("");
    }

    private void waitForDelay(long delayMs) {

        if (delayMs <= 0) {
            log.info(
                    "Delay is 0 ms. Continuing immediately."
            );

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