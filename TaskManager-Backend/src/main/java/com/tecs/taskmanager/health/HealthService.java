package com.tecs.taskmanager.health;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tecs.taskmanager.core.snapshot.SystemSnapshotService;
import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;
import com.tecs.taskmanager.dto.disk.DiskInfoDTO;
import com.tecs.taskmanager.dto.disk.PartitionDTO;
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

        double cpu = round(snapshot.getCpu().getSystemLoad());
        components.add(evaluate("CPU", cpu, 70, 90));

        double memory = round(snapshot.getMemory().getUsagePercent());
        components.add(evaluate("Memory", memory, 75, 90));

        if (snapshot.getDisks() != null) {

            for (DiskInfoDTO disk : snapshot.getDisks()) {

                if (disk.getPartitions() == null) continue;

                for (PartitionDTO partition : disk.getPartitions()) {

                    double usage = round(partition.getUsagePercent());

                    HealthStatus status;

                    if (usage > 90) {
                        status = HealthStatus.CRITICAL;
                    } else if (usage > 75) {
                        status = HealthStatus.WARNING;
                    } else {
                        status = HealthStatus.UP;
                    }

                    String mount = partition.getMountPoint()
                            .replace("\\", "")
                            .trim();

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

        boolean networkUp = snapshot.getNetworks() != null && !snapshot.getNetworks().isEmpty();

        components.add(ComponentHealthDTO.builder()
                .component("Network")
                .healthStatus(networkUp ? HealthStatus.UP : HealthStatus.WARNING)
                .message(networkUp
                        ? "Network Active"
                        : "No active network interface")
                .build());

        evaluateBattery(snapshot, components);

        if (snapshot.getDatabases() == null
                || snapshot.getDatabases().isEmpty()) {

            components.add(ComponentHealthDTO.builder()
                    .component("Database")
                    .healthStatus(HealthStatus.CRITICAL)
                    .message("No database detected")
                    .build());
        } else {
            components.add(ComponentHealthDTO.builder()
                    .component("Database")
                    .healthStatus(HealthStatus.UP)
                    .message("Databases running")
                    .build());
        }

        HealthStatus overall = calculateOverall(components);

        return SystemHealthDTO.builder()
                .overallStatus(overall)
                .components(components)
                .build();
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
