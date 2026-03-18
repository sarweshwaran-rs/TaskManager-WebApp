package com.tecs.taskmanager.dto.health;

import java.util.List;

import com.tecs.taskmanager.health.HealthStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SystemHealthDTO {
    private HealthStatus overallStatus;
    private List<ComponentHealthDTO> components;
}
