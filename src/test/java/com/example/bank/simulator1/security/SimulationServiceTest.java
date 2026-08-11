package com.example.bank.simulator1.security;

import org.junit.jupiter.api.Test;

import com.example.bank.simulator1.repository.SimulationConfigRepository;
import com.example.bank.simulator1.service.SimulationService;
import com.example.bank.simulator1.state.SimulationMode;
import com.example.bank.simulator1.state.TransactionStatus;

import static org.junit.jupiter.api.Assertions.*;

class SimulationServiceTest {

	private static final SimulationConfigRepository SimulationConfigRepository = null;
	private final SimulationService simulationService = new SimulationService(SimulationConfigRepository);

	@Test
	void normalModeShouldReturnSuccess() {

		assertEquals(TransactionStatus.SUCCESS, simulationService.determineStatus(SimulationMode.NORMAL));
	}

	@Test
	void forceSuccessShouldReturnSuccess() {

		assertEquals(TransactionStatus.SUCCESS, simulationService.determineStatus(SimulationMode.FORCE_SUCCESS));
	}

	@Test
	void forceFailureShouldReturnFailure() {

		assertEquals(TransactionStatus.FAILURE, simulationService.determineStatus(SimulationMode.FORCE_FAILURE));
	}

	@Test
	void nullModeShouldDefaultToSuccess() {

		assertEquals(TransactionStatus.SUCCESS, simulationService.determineStatus(null));
	}
}