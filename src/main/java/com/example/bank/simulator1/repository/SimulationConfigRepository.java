package com.example.bank.simulator1.repository;


import org.springframework.stereotype.Repository;

import com.example.bank.simulator1.model.SimulationConfig;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SimulationConfigRepository {

    private final Map<String, SimulationConfig> configurations =
            new ConcurrentHashMap<>();

    public SimulationConfig save(SimulationConfig config) {
        configurations.put(config.getPrn(), config);
        return config;
    }

    public Optional<SimulationConfig> findByPrn(String prn) {
        return Optional.ofNullable(configurations.get(prn));
    }

    public void deleteByPrn(String prn) {
        configurations.remove(prn);
    }
}