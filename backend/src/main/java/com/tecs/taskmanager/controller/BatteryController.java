package com.tecs.taskmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecs.taskmanager.core.monitoing.battery.BatteryMonitor;
import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;
import com.tecs.taskmanager.dto.common.ApiResponse;

@RestController
public class BatteryController {

    private final BatteryMonitor batteryMonitor;

    public BatteryController(BatteryMonitor batteryMonitor) {
        this.batteryMonitor = batteryMonitor;
    }

    @GetMapping("/api/battery")
    public ResponseEntity<ApiResponse<List<BatteryInfoDTO>>> getBatteryInfo() {

        List<BatteryInfoDTO> data = batteryMonitor.getBatteryInfo();

        if (data == null || data.isEmpty()) {
            throw new RuntimeException("Battery info not available");
        }

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
