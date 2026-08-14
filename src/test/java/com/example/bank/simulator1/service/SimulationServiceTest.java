package com.example.bank.simulator1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.repository.SimulationConfigRepository;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private SimulationConfigRepository configRepository;

    private SimulationService simulationService;

    @BeforeEach
    void setUp() {
        simulationService =
                new SimulationService(configRepository);
    }

    // =====================================================
    // TEST 1: Configure simulation
    // =====================================================

    @Test
    void shouldConfigureSimulation() {

        simulationService.configure(
                "PRN001",
                SimulationMode.SUCCESS,
                0
        );

        verify(configRepository)
                .save(any(SimulationConfig.class));
    }

    // =====================================================
    // TEST 2: Configure with delay
    // =====================================================

    @Test
    void shouldConfigureSimulationWithDelay() {

        simulationService.configure(
                "PRN002",
                SimulationMode.DELAY,
                5000
        );

        verify(configRepository)
                .save(any(SimulationConfig.class));
    }

    // =====================================================
    // TEST 3: Get existing configuration
    // =====================================================

    @Test
    void shouldReturnExistingConfiguration() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN001",
                        SimulationMode.SUCCESS,
                        0
                );

        when(configRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(config));

        SimulationConfig result =
                simulationService.getConfiguration("PRN001");

        assertNotNull(result);

        assertEquals(
                "PRN001",
                result.getPrn()
        );

        assertEquals(
                SimulationMode.SUCCESS,
                result.getSimulationMode()
        );

        assertEquals(
                0,
                result.getDelayMs()
        );

        verify(configRepository)
                .findByPrn("PRN001");
    }

    // =====================================================
    // TEST 4: Return null when configuration doesn't exist
    // =====================================================

    @Test
    void shouldReturnNullWhenConfigurationDoesNotExist() {

        when(configRepository.findByPrn("PRN999"))
                .thenReturn(Optional.empty());

        SimulationConfig result =
                simulationService.getConfiguration("PRN999");

        assertNull(result);

        verify(configRepository)
                .findByPrn("PRN999");
    }

    // =====================================================
    // TEST 5: Clear configuration
    // =====================================================

    @Test
    void shouldClearConfiguration() {

        simulationService.clearConfiguration("PRN001");

        verify(configRepository)
                .deleteByPrn("PRN001");
    }

    // =====================================================
    // TEST 6: No configuration = SUCCESS
    // =====================================================

    @Test
    void shouldReturnSuccessWhenNoConfigurationExists() {

        when(configRepository.findByPrn("PRN001"))
                .thenReturn(Optional.empty());

        TransactionStatus result =
                simulationService.determineStatus("PRN001");

        assertEquals(
                TransactionStatus.SUCCESS,
                result
        );
    }

    // =====================================================
    // TEST 7: SUCCESS scenario
    // =====================================================

    @Test
    void shouldReturnSuccessForSuccessMode() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN001",
                        SimulationMode.SUCCESS,
                        0
                );

        when(configRepository.findByPrn("PRN001"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN001");

        assertEquals(
                TransactionStatus.SUCCESS,
                result
        );
    }

    // =====================================================
    // TEST 8: FAILURE scenario
    // =====================================================

    @Test
    void shouldReturnFailureForFailureMode() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN002",
                        SimulationMode.FAILURE,
                        0
                );

        when(configRepository.findByPrn("PRN002"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN002");

        assertEquals(
                TransactionStatus.FAILURE,
                result
        );
    }

    // =====================================================
    // TEST 9: PENDING scenario
    // =====================================================

    @Test
    void shouldReturnPendingForPendingMode() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN003",
                        SimulationMode.PENDING,
                        0
                );

        when(configRepository.findByPrn("PRN003"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN003");

        assertEquals(
                TransactionStatus.PENDING,
                result
        );
    }

    // =====================================================
    // TEST 10: DELAY scenario
    // =====================================================

    @Test
    void shouldReturnSuccessForDelayMode() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN004",
                        SimulationMode.DELAY,
                        5000
                );

        when(configRepository.findByPrn("PRN004"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN004");

        assertEquals(
                TransactionStatus.SUCCESS,
                result
        );
    }

    // =====================================================
    // TEST 11: DROP scenario
    // =====================================================

    @Test
    void shouldReturnSuccessForDropMode() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN005",
                        SimulationMode.DROP,
                        0
                );

        when(configRepository.findByPrn("PRN005"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN005");

        assertEquals(
                TransactionStatus.SUCCESS,
                result
        );
    }

    // =====================================================
    // TEST 12: DUPLICATE scenario
    // =====================================================

    @Test
    void shouldReturnSuccessForDuplicateMode() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN006",
                        SimulationMode.DUPLICATE,
                        0
                );

        when(configRepository.findByPrn("PRN006"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN006");

        assertEquals(
                TransactionStatus.SUCCESS,
                result
        );
    }

    // =====================================================
    // TEST 13: Null simulation mode
    // =====================================================

    @Test
    void shouldReturnSuccessWhenSimulationModeIsNull() {

        SimulationConfig config =
                new SimulationConfig(
                        "PRN007",
                        null,
                        0
                );

        when(configRepository.findByPrn("PRN007"))
                .thenReturn(Optional.of(config));

        TransactionStatus result =
                simulationService.determineStatus("PRN007");

        assertEquals(
                TransactionStatus.SUCCESS,
                result
        );
    }
}
