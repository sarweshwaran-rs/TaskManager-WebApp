package com.tecs.taskmanager.health;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tecs.taskmanager.core.snapshot.SnapshotCacheService;
import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;
import com.tecs.taskmanager.dto.disk.DiskInfoDTO;
import com.tecs.taskmanager.dto.disk.PartitionDTO;
import com.tecs.taskmanager.dto.health.ComponentHealthDTO;
import com.tecs.taskmanager.dto.health.SystemHealthDTO;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

@Service
public class HealthService {

    private final SnapshotCacheService cacheService;

    public HealthService(SnapshotCacheService cacheService) {
        this.cacheService = cacheService;
    }

    public SystemHealthDTO evaluateHealth() {

        SystemSnapshotDTO snapshot = cacheService.getSnapshot();

        if (snapshot == null) {
            return SystemHealthDTO.builder()
                .overallStatus(HealthStatus.CRITICAL)
                .components(Collections.emptyList())
                .build();
        }

        List<ComponentHealthDTO> components = new ArrayList<>();

        evaluateCpu(snapshot, components);
        evaluateMemory(snapshot, components);
        evaluateDisks(snapshot, components);
        evaluateNetwork(snapshot, components);
        evaluateBattery(snapshot, components);
        evaluateDatabase(snapshot, components);

        HealthStatus overall = calculateOverall(components);

        return SystemHealthDTO.builder()
                .overallStatus(overall)
                .components(components)
                .build();
    }

    private void evaluateCpu(SystemSnapshotDTO snapshot, List<ComponentHealthDTO> components) {

        if (snapshot.getCpu() == null)
            return;

        double cpuLoad = round(snapshot.getCpu().getSystemLoad());
        components.add(evaluate("CPU", cpuLoad, 70, 90));
    }

    private void evaluateMemory(SystemSnapshotDTO snapshot, List<ComponentHealthDTO> components) {

        if (snapshot.getMemory() == null)
            return;

        double memoryUsage = round(snapshot.getMemory().getUsagePercent());
        components.add(evaluate("Memory", memoryUsage, 75, 90));
    }

    private void evaluateDisks(SystemSnapshotDTO snapshot, List<ComponentHealthDTO> components) {

        if (snapshot.getDisks() == null)
            return;

        for (DiskInfoDTO disk : snapshot.getDisks()) {

            if (disk.getPartitions() == null)
                continue;

            for (PartitionDTO partition : disk.getPartitions()) {

                double usage = round(partition.getUsagePercent());

                HealthStatus status = usage > 90 ? HealthStatus.CRITICAL
                        : usage > 75 ? HealthStatus.WARNING : HealthStatus.UP;

                String mount = partition.getMountPoint() == null ? "Unknown" : partition.getMountPoint().replace("\\", "").trim();

                components.add(ComponentHealthDTO.builder()
                        .component("Disk (" + mount + ")")
                        .healthStatus(status)
                        .message(String.format(
                                "%s usage: %.2f%% (Used: %.2f GB / %.2f GB)",
                                mount,
                                usage,
                                partition.getUsedGB(),
                                partition.getTotalGB()))
                        .build());
            }
        }
    }

    private void evaluateNetwork(SystemSnapshotDTO snapshot, List<ComponentHealthDTO> components) {

        boolean networkUp = snapshot.getNetworks() != null && !snapshot.getNetworks().isEmpty();

        components.add(ComponentHealthDTO.builder()
                .component("Network")
                .healthStatus(networkUp ? HealthStatus.UP : HealthStatus.WARNING)
                .message(networkUp ? "Network Active" : "No active network interface")
                .build());
    }

    private void evaluateBattery(SystemSnapshotDTO snapshot, List<ComponentHealthDTO> components) {

        if (snapshot.getBatteries() == null || snapshot.getBatteries().isEmpty()) {
            return;
        }

        BatteryInfoDTO battery = snapshot.getBatteries().get(0);

        double percent = round(battery.getRemainingPercent());
        double health = round(battery.getHealthPercent());
        boolean charging = battery.isCharging();

        if (percent < 20 && !charging) {
            components.add(build("Battery", HealthStatus.CRITICAL, "Battery low and discharging (" + percent + "%)"));
        } else if (health < 40) {
            components.add(build("Battery", HealthStatus.WARNING, "Battery health low (" + health + "%)"));
        } else {
            components.add(build("Battery", HealthStatus.UP, "Battery normal"));
        }
    }

    private void evaluateDatabase(SystemSnapshotDTO snapshot, List<ComponentHealthDTO> components) {
        
        boolean dbUp = snapshot.getDatabases() != null && !snapshot.getDatabases().isEmpty();

        components.add(ComponentHealthDTO.builder()
                .component("Database")
                .healthStatus(dbUp ? HealthStatus.UP : HealthStatus.CRITICAL)
                .message(dbUp ? "Databases running" : "No database detected")
                .build());
    }

    private ComponentHealthDTO evaluate(String component, double value, double warningThreshold, double criticalThreshold) {
        if (value > criticalThreshold) {
            return build(component, HealthStatus.CRITICAL, component + " usage critical: " + value + "%");
        }

        if (value > warningThreshold) {
            return build(component, HealthStatus.WARNING, component + " usage high: " + value + "%");
        }

        return build(component, HealthStatus.UP, component + " normal");
    }

    private ComponentHealthDTO build(String name, HealthStatus status, String message) {
        return ComponentHealthDTO.builder()
                .component(name)
                .healthStatus(status)
                .message(message)
                .build();
    }

    private HealthStatus calculateOverall(List<ComponentHealthDTO> components) {
        
        if (components.stream()
                .anyMatch(c -> c.getHealthStatus() == HealthStatus.CRITICAL)) {
            return HealthStatus.CRITICAL;
        }

        if (components.stream()
                .anyMatch(c -> c.getHealthStatus() == HealthStatus.WARNING)) {
            return HealthStatus.WARNING;
        }
        return HealthStatus.UP;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
