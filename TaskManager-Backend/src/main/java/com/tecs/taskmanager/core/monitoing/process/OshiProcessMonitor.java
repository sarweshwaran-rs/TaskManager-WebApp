package com.tecs.taskmanager.core.monitoing.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.process.ProcessInfoDTO;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

@Component
public class OshiProcessMonitor implements ProcessMonitor{
    private final SystemInfo systemInfo;
    private final OperatingSystem os;
    private Map<Integer, OSProcess> previousSnapshot = new ConcurrentHashMap<>();

    public OshiProcessMonitor() {
        this.systemInfo = new SystemInfo();
        this.os = systemInfo.getOperatingSystem();
    }

    @Override
    public List<ProcessInfoDTO> getTopProcesses(int limit) {
        List<OSProcess> processes = os.getProcesses();
        Map<Integer, OSProcess> currentSnapshot = new HashMap<>();

        List<ProcessInfoDTO> processList = processes.stream()
        .map(process -> {
            currentSnapshot.put(process.getProcessID(), process);
            OSProcess previous = previousSnapshot.get(process.getProcessID());

            // Handle PID resue
            if(previous != null && process.getStartTime() != previous.getStartTime()) {
                previous = null;
            }

            double cpuLoad = process.getProcessCpuLoadBetweenTicks(previous) * 100.0 / systemInfo.getHardware().getProcessor().getLogicalProcessorCount();
            double roundedCpu = Math.round(cpuLoad * 100.0) / 100.0;

            return new ProcessInfoDTO(
                process.getProcessID(), 
                process.getName(),
                roundedCpu,
                process.getResidentSetSize()
            );
        })
        .sorted((p1, p2) -> Double.compare(p2.getCpuLoad(), p1.getCpuLoad()))
        .limit(limit)
        .collect(Collectors.toList());

        previousSnapshot.clear();
        previousSnapshot.putAll(currentSnapshot);
        return processList;
    }
}
