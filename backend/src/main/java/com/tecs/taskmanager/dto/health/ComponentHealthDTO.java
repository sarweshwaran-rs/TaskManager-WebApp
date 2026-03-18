package com.tecs.taskmanager.dto.health;

import com.tecs.taskmanager.health.HealthStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ComponentHealthDTO {
    private String component;
    private HealthStatus healthStatus;
    private String message;
}
