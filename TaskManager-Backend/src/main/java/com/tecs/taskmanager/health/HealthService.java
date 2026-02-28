package com.tecs.taskmanager.health;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tecs.taskmanager.core.snapshot.SystemSnapshotService;
import com.tecs.taskmanager.dto.health.ComponentHealthDTO;
import com.tecs.taskmanager.dto.health.SystemHealthDTO;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

@Service
public class HealthService {
    private final SystemSnapshotService snapshotService;

    public HealthService(SystemSnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    public SystemHealthDTO evaluateHealth() {
        SystemSnapshotDTO snapshot = snapshotService.getSnapshot();

        List<ComponentHealthDTO> components = new ArrayList<>();

        double cpu = snapshot.getCpu().getSystemLoad();
        components.add(evaluate("CPU", cpu, 70, 90));

        double memoryUsedPercent = (snapshot.getMemory().getUsedGB() /
                snapshot.getMemory().getTotalGB()) * 100;
        components.add(evaluate("Memory", memoryUsedPercent, 75, 90));

        if (snapshot.getDatabases().isEmpty()) {
            components.add(ComponentHealthDTO.builder()
                    .component("Database")
                    .healthStatus(HealthStatus.WARNING)
                    .message("No database detected").build());
        } else {
            components.add(ComponentHealthDTO.builder()
                    .component("Database")
                    .healthStatus(HealthStatus.UP)
                    .message("Databases running").build());
        }

        HealthStatus overall = calculateOverall(components);

        return SystemHealthDTO.builder()
                .overallStatus(overall)
                .components(components)
                .build();
    }

    private ComponentHealthDTO evaluate(String component, double value, double warningThreshold,
            double criticalThreshold) {
        if (value > criticalThreshold) {
            return ComponentHealthDTO.builder()
                    .component(component)
                    .healthStatus(HealthStatus.CRITICAL)
                    .message(component + " usage critical: " + value + "%")
                    .build();
        } else if (value > warningThreshold) {
            return ComponentHealthDTO.builder()
                    .component(component)
                    .healthStatus(HealthStatus.WARNING)
                    .message(component + " usage high: " + value + "%")
                    .build();
        } else {
            return ComponentHealthDTO.builder()
                    .component(component)
                    .healthStatus(HealthStatus.UP)
                    .message(component + " normal")
                    .build();
        }
    }

    private HealthStatus calculateOverall(List<ComponentHealthDTO> components) {
        if (components.stream().anyMatch(c -> c.getHealthStatus() == HealthStatus.CRITICAL))
            return HealthStatus.CRITICAL;
        if (components.stream().anyMatch(c -> c.getHealthStatus() == HealthStatus.WARNING))
            return HealthStatus.WARNING;

        return HealthStatus.UP;
    }
}
