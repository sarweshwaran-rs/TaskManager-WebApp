package com.tecs.taskmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecs.taskmanager.dto.common.ApiResponse;
import com.tecs.taskmanager.dto.health.SystemHealthDTO;
import com.tecs.taskmanager.health.HealthService;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<ApiResponse<SystemHealthDTO>> getHealth() {
        SystemHealthDTO health = healthService.evaluateHealth();

        if (health == null) {
            throw new RuntimeException("Health data not available");
        }

        return ResponseEntity.ok(ApiResponse.success(health));
    }
}
