package com.tecs.taskmanager.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tecs.taskmanager.dto.health.SystemHealthDTO;
import com.tecs.taskmanager.health.HealthService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/api/health")
    public SystemHealthDTO getHealth() {
        return healthService.evaluateHealth();
    }
}
