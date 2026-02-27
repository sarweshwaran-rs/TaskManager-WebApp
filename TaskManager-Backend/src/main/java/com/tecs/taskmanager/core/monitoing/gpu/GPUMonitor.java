package com.tecs.taskmanager.core.monitoing.gpu;

import java.util.List;

import com.tecs.taskmanager.dto.gpu.GPUInfoDTO;

public interface GPUMonitor {
    List<GPUInfoDTO> getGPUInfo();
}
