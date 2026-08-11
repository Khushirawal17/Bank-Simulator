package com.example.bank.simulator1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bank.simulator1.service.SimulationService;

@RestController
@RequestMapping("/control")
public class ControlController {

	private final SimulationService simulationService;

	public ControlController(SimulationService simulationService) {

		this.simulationService = simulationService;
	}

}