package com.example.bank.simulator1.model;

import com.example.bank.simulator1.state.SimulationMode;

public class SimulationConfig {

	private String prn;
	private SimulationMode simulationMode;
	private long delayMs;

	public SimulationConfig() {
	}

	public SimulationConfig(String prn, SimulationMode simulationMode, long delayMs) {

		this.prn = prn;
		this.simulationMode = simulationMode;
		this.delayMs = delayMs;
	}

	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public SimulationMode getSimulationMode() {
		return simulationMode;
	}

	public void setSimulationMode(SimulationMode simulationMode) {
		this.simulationMode = simulationMode;
	}

	public long getDelayMs() {
		return delayMs;
	}

	public void setDelayMs(long delayMs) {
		this.delayMs = delayMs;
	}
}