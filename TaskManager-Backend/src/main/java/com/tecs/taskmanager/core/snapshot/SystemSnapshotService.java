package com.tecs.taskmanager.core.snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tecs.taskmanager.core.monitoing.battery.BatteryMonitor;
import com.tecs.taskmanager.core.monitoing.cpu.CPUMonitor;
import com.tecs.taskmanager.core.monitoing.database.DatabaseMonitor;
import com.tecs.taskmanager.core.monitoing.disk.DiskMonitor;
import com.tecs.taskmanager.core.monitoing.gpu.GPUMonitor;
import com.tecs.taskmanager.core.monitoing.memory.MemoryMonitor;
import com.tecs.taskmanager.core.monitoing.network.NetworkMonitor;
import com.tecs.taskmanager.core.monitoing.os.OSMonitor;
import com.tecs.taskmanager.core.monitoing.process.ProcessMonitor;
import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessSectionDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

@Service
public class SystemSnapshotService {

    private final CPUMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;
    private final DiskMonitor diskMonitor;
    private final NetworkMonitor networkMonitor;
    private final BatteryMonitor batteryMonitor;
    private final GPUMonitor gpuMonitor;
    private final ProcessMonitor processMonitor;
    private final OSMonitor osMonitor;
    private final DatabaseMonitor databaseMonitor;

    public SystemSnapshotService(CPUMonitor cpuMonitor, MemoryMonitor memoryMonitor, DiskMonitor diskMonitor, NetworkMonitor networkMonitor, BatteryMonitor batteryMonitor, GPUMonitor gpuMonitor, ProcessMonitor processMonitor, OSMonitor osMonitor, DatabaseMonitor databaseMonitor) {
        this.cpuMonitor = cpuMonitor;
        this.memoryMonitor = memoryMonitor;
        this.diskMonitor = diskMonitor;
        this.networkMonitor = networkMonitor;
        this.batteryMonitor = batteryMonitor;
        this.gpuMonitor = gpuMonitor;
        this.processMonitor = processMonitor;
        this.osMonitor = osMonitor;
        this.databaseMonitor = databaseMonitor;
    }

    public SystemSnapshotDTO buildSnapshot() {

        var processes = processMonitor.getAllProcesses();
        var processTree = processMonitor.getProcessTree();

        var grouped = buildGrouped(processTree);
        var sections = buildSections(grouped);

        return new SystemSnapshotDTO(
                cpuMonitor.getCpuInfo(),
                memoryMonitor.getMemoryInfo(),
                osMonitor.getOSInfo(),
                processes,
                processTree,
                batteryMonitor.getBatteryInfo(),
                gpuMonitor.getGPUInfo(),
                diskMonitor.getDiskInfo(),
                networkMonitor.getNetworkInfo(),
                databaseMonitor.detectDatabases(),
                grouped,
                sections
            );
    }

    private List<ProcessGroupDTO> buildGrouped(List<ProcessTreeDTO> tree) {

        Map<String, ProcessGroupDTO> grouped = new HashMap<>();

        for (ProcessTreeDTO root : tree) {

            String key = root.getPath() != null && !root.getPath().isEmpty()
                    ? root.getPath()
                    : root.getName();

            grouped.computeIfAbsent(key,
                    k -> new ProcessGroupDTO(
                            root.getName(),
                            root.getType(),
                            0,
                            0.0,
                            0L,
                            new ArrayList<>())
                        );

            ProcessGroupDTO group = grouped.get(key);

            group.getInstances().add(root);
            group.setCount(group.getCount() + 1);
            group.setTotalCPU(group.getTotalCPU() + root.getCpuLoad());
            group.setTotalMemory(group.getTotalMemory() + root.getMemory());
        }

        return new ArrayList<>(grouped.values());
    }

    private List<ProcessSectionDTO> buildSections(List<ProcessGroupDTO> grouped) {

        Map<String, List<ProcessGroupDTO>> sections = new HashMap<>();

        for (ProcessGroupDTO group : grouped) {

            String section;

            switch (group.getType()) {

                case "APPLICATION":
                    section = "Applications";
                    break;

                case "WINDOWS_PROCESS":
                case "WINDOWS_SERVICE":
                case "SYSTEM_PROCESS":
                    section = "Windows Processes";
                    break;

                case "SYSTEM_SERVICE":
                    section = "System Services";
                    break;

                default:
                    section = "Background Process";
            }

            sections.computeIfAbsent(section, k -> new ArrayList<>())
                    .add(group);
        }

        return sections.entrySet()
                .stream()
                .map(entry -> ProcessSectionDTO.builder()
                        .sectionName(entry.getKey())
                        .totalCount(entry.getValue().size())
                        .groups(entry.getValue())
                        .build())
                .toList();
    }
}
