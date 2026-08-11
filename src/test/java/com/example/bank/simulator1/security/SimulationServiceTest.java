package com.example.bank.simulator1.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.repository.SimulationConfigRepository;
import com.example.bank.simulator1.service.SimulationService;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

class SimulationServiceTest {

    private SimulationConfigRepository configRepository;
    private SimulationService simulationService;

    @BeforeEach
    void setUp() {
        configRepository = new SimulationConfigRepository();
        simulationService = new SimulationService(configRepository);
    }

    @Test
    void normalModeShouldReturnSuccess() {

        configRepository.save(
                new SimulationConfig(
                        "PRN001",
                        SimulationMode.NORMAL,
                        0
                )
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                simulationService.determineStatus("PRN001")
        );
    }

    @Test
    void forceSuccessShouldReturnSuccess() {

        configRepository.save(
                new SimulationConfig(
                        "PRN002",
                        SimulationMode.FORCE_SUCCESS,
                        0
                )
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                simulationService.determineStatus("PRN002")
        );
    }

    @Test
    void forceFailureShouldReturnFailure() {

        configRepository.save(
                new SimulationConfig(
                        "PRN003",
                        SimulationMode.FORCE_FAILURE,
                        0
                )
        );

        assertEquals(
                TransactionStatus.FAILURE,
                simulationService.determineStatus("PRN003")
        );
    }

    @Test
    void forcePendingShouldReturnPending() {

        configRepository.save(
                new SimulationConfig(
                        "PRN004",
                        SimulationMode.FORCE_PENDING,
                        0
                )
        );

        assertEquals(
                TransactionStatus.PENDING,
                simulationService.determineStatus("PRN004")
        );
    }

    @Test
    void missingConfigurationShouldDefaultToSuccess() {

        assertEquals(
                TransactionStatus.SUCCESS,
                simulationService.determineStatus("PRN999")
        );
    }
}