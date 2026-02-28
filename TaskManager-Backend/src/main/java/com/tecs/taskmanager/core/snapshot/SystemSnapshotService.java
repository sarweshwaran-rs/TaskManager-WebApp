package com.tecs.taskmanager.core.snapshot;

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

    public SystemSnapshotDTO getSnapshot() {
        return new SystemSnapshotDTO(
            cpuMonitor.getCpuInfo(),
            memoryMonitor.getMemoryInfo(),
            osMonitor.getOSInfo(),
            processMonitor.getAllProcesses(),
            processMonitor.getProcessTree(),
            batteryMonitor.getBatteryInfo(),
            gpuMonitor.getGPUInfo(),
            diskMonitor.getDiskInfo(),
            networkMonitor.getNetworkInfo(),
            databaseMonitor.detectDatabases()
        );
    }
}
