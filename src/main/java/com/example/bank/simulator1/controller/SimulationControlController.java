package com.example.bank.simulator1.controller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bank.simulator1.dto.SimulationOverrideRequest;
import com.example.bank.simulator1.model.SimulationConfig;
import com.example.bank.simulator1.service.SimulationService;

@RestController
@RequestMapping("/control")
public class SimulationControlController {

    private final SimulationService simulationService;

    public SimulationControlController(
            SimulationService simulationService) {

        this.simulationService = simulationService;
    }

    @PostMapping("/override")
    public ResponseEntity<SimulationConfig> configureOverride(
            @Valid @RequestBody
            SimulationOverrideRequest request) {

        simulationService.configure(
                request.getPrn(),
                request.getSimulationMode(),
                request.getDelayMs()
        );

        return ResponseEntity.ok(
                simulationService.getConfiguration(
                        request.getPrn()
                )
        );
    }

    @GetMapping("/override/{prn}")
    public ResponseEntity<SimulationConfig> getOverride(
            @PathVariable String prn) {

        SimulationConfig config =
                simulationService.getConfiguration(prn);

        if (config == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/override/{prn}")
    public ResponseEntity<Void> clearOverride(
            @PathVariable String prn) {

        simulationService.clearConfiguration(prn);

        return ResponseEntity.noContent().build();
    }
}