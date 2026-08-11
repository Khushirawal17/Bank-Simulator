package com.example.bank.simulator1.dto;


import com.example.bank.simulator1.state.SimulationMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class SimulationOverrideRequest {

    @NotBlank
    private String prn;

    @NotNull
    private SimulationMode simulationMode;

    @PositiveOrZero
    private long delayMs;

    public SimulationOverrideRequest() {
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

    public void setSimulationMode(
            SimulationMode simulationMode) {

        this.simulationMode = simulationMode;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
}