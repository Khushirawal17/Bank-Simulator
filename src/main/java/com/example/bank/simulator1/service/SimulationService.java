package com.example.bank.simulator1.service;


import org.springframework.stereotype.Service;

import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.repository.SimulationConfigRepository;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

@Service
public class SimulationService {

    private final SimulationConfigRepository configRepository;

    public SimulationService(
            SimulationConfigRepository configRepository) {
            this.configRepository = configRepository;
    }

    public void configure(
            String prn,
            SimulationMode mode,
            long delayMs) {

        SimulationConfig config =
                new SimulationConfig(
                        prn,
                        mode,
                        delayMs
                );

        configRepository.save(config);
    }

    public void clearConfiguration(String prn) {
        configRepository.deleteByPrn(prn);
    }

    public SimulationConfig getConfiguration(String prn) {
        return configRepository
                .findByPrn(prn)
                .orElse(null);
    }

    public TransactionStatus determineStatus(
            String prn) {

        SimulationConfig config =
                getConfiguration(prn);

        if (config == null ||
                config.getSimulationMode() == null) {

            return TransactionStatus.SUCCESS;
        }

        return switch (config.getSimulationMode()) {

            case NORMAL, FORCE_SUCCESS ->
                    TransactionStatus.SUCCESS;

            case FORCE_FAILURE ->
                    TransactionStatus.FAILURE;

            case DELAY, DROP, DUPLICATE ->
                    TransactionStatus.SUCCESS;
                    
            case FORCE_PENDING ->
            TransactionStatus.PENDING;
        };
    }
}
