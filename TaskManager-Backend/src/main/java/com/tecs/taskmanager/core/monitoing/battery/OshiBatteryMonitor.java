package com.tecs.taskmanager.core.monitoing.battery;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;

import oshi.SystemInfo;
import oshi.hardware.PowerSource;

@Component
public class OshiBatteryMonitor implements BatteryMonitor {
    private final SystemInfo systemInfo;

    public OshiBatteryMonitor() {
        this.systemInfo = new SystemInfo();
    }

    @Override
    public List<BatteryInfoDTO> getBatteryInfo() {
        List<PowerSource> sources = systemInfo.getHardware().getPowerSources();
        if (sources == null || sources.size() == 0) {
            return List.of();
        }

        return sources.stream()
                .map(this::mapToDO)
                .toList();
    }

    private BatteryInfoDTO mapToDO(PowerSource ps) {

        double design = ps.getDesignCapacity();
        double max = ps.getMaxCapacity();

        double health = 0;
        if (design > 0 && max > 0) {
            health = (max * 100.0) / design;
        }

        double power = ps.getPowerUsageRate();
        String direction = power > 0 ? "CHARGING" : power < 0 ? "DISCHARGING" : "IDLE";

        return BatteryInfoDTO.builder()
                .name(ps.getName())
                .remainingPercent(round(ps.getRemainingCapacityPercent() * 100))
                .charging(ps.isCharging())
                .powerUsageRate(round(Math.abs(power) / 1000.0)) // convert mW → W
                .voltage(round(ps.getVoltage()))
                .designCapacity(design)
                .maxCapacity(max)
                .currentCapacity(ps.getCurrentCapacity())
                .cycleCount(ps.getCycleCount())
                .healthPercent(round(health))
                .timeRemaining(ps.getTimeRemainingEstimated())
                .powerDirection(direction) // add this field
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
