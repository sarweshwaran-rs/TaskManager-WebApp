package com.tecs.taskmanager.dto.battery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatteryInfoDTO {
    private String name;
    
    private double remainingPercent;
    private boolean charging;
    private double powerUsageRate;
    
    private double voltage;
    private double designCapacity;
    private double maxCapacity;
    private double currentCapacity;

    private long cycleCount;
    private double healthPercent;
    private double timeRemaining;
    private String powerDirection;
}
