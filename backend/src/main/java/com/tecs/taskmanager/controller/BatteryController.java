package com.tecs.taskmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tecs.taskmanager.core.monitoing.battery.BatteryMonitor;
import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;

@RestController
public class BatteryController {

    private final com.tecs.taskmanager.core.monitoing.battery.BatteryMonitor batteryMonitor;

    public BatteryController(BatteryMonitor batteryMonitor) {
        this.batteryMonitor = batteryMonitor;
    }

    @GetMapping("/api/battery")
    public List<BatteryInfoDTO> getBatteryInfo() {
        return batteryMonitor.getBatteryInfo();
    }
}