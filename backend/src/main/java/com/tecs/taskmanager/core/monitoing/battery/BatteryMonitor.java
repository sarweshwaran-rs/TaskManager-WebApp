package com.tecs.taskmanager.core.monitoing.battery;

import java.util.List;

import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;

public interface BatteryMonitor {
    List<BatteryInfoDTO> getBatteryInfo();
}
