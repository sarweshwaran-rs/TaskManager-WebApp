package com.tecs.taskmanager.core.monitoing.memory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.memory.MemoryInfoDTO;
import com.tecs.taskmanager.dto.memory.PhysicalMemoryDTO;

import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

@Component
public class OshiMemoryMonitor implements MemoryMonitor {
    private final GlobalMemory memory;
    private final VirtualMemory virtualMemory;

    public OshiMemoryMonitor(GlobalMemory globalMemory, VirtualMemory virtualMemory) {
        this.memory = globalMemory;
        this.virtualMemory = virtualMemory;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public MemoryInfoDTO getMemoryInfo() {
        double totalGB = round(memory.getTotal() / (1024.0 * 1024.0 * 1024.0));
        double availableGB = round(memory.getAvailable() / (1024.0 * 1024.0 * 1024.0));
        double usedGB = round(totalGB - availableGB);
        double swapTotalGB = round(virtualMemory.getSwapTotal() / (1024.0 * 1024.0 * 1024.0));
        double swapUsedGB = round(virtualMemory.getSwapUsed() / (1024.0 * 1024.0 * 1024.0));
        double usagePercent = round((usedGB / totalGB) * 100.0);

        List<PhysicalMemoryDTO> physicalMemoryDTOs = memory.getPhysicalMemory().stream()
                .map(pm -> PhysicalMemoryDTO.builder()
                        .bankLabel(pm.getBankLabel())
                        .capacityGB(pm.getCapacity() / (1024.0 * 1024.0 * 1024.0))
                        .clockSpeed(pm.getClockSpeed())
                        .manufacturer(pm.getManufacturer())
                        .memoryType(pm.getMemoryType())
                        .build())
                .toList();

        return MemoryInfoDTO.builder()
                .totalGB(totalGB)
                .availableGB(availableGB)
                .usedGB(usedGB)
                .swapTotalGB(swapTotalGB)
                .swapUsedGB(swapUsedGB)
                .usagePercent(usagePercent)
                .physicalMemory(physicalMemoryDTOs)
                .build();
    }
}
