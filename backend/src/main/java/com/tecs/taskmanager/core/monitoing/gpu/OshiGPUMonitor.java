package com.tecs.taskmanager.core.monitoing.gpu;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.gpu.GPUInfoDTO;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;

@Component
public class OshiGPUMonitor implements GPUMonitor {
    private final SystemInfo systemInfo;

    public OshiGPUMonitor(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<GPUInfoDTO> getGPUInfo() {
        List<GraphicsCard> gpus = systemInfo.getHardware().getGraphicsCards();
        return gpus.stream()
                .map(gpu -> {
                    double vramGB = gpu.getVRam() / (1024.0 * 1024.0 * 1024.0);

                    return GPUInfoDTO.builder()
                            .deviceId(gpu.getDeviceId())
                            .name(gpu.getName())
                            .vendor(gpu.getVendor())
                            .vram(String.format("%.1f GB", vramGB))
                            .versionInfo(gpu.getVersionInfo())
                            .build();
                }).collect(Collectors.toList());
    }
}
