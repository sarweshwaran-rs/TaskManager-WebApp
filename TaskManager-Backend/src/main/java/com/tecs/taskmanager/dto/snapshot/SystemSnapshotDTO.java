package com.tecs.taskmanager.dto.snapshot;

import java.util.List;

import com.tecs.taskmanager.dto.battery.BatteryInfoDTO;
import com.tecs.taskmanager.dto.cpu.CpuInfoDTO;
import com.tecs.taskmanager.dto.database.DBInfo;
import com.tecs.taskmanager.dto.disk.DiskInfoDTO;
import com.tecs.taskmanager.dto.gpu.GPUInfoDTO;
import com.tecs.taskmanager.dto.memory.MemoryInfoDTO;
import com.tecs.taskmanager.dto.network.NetworkInfoDTO;
import com.tecs.taskmanager.dto.os.OSInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessSectionDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSnapshotDTO {

    private CpuInfoDTO cpu;
    private MemoryInfoDTO memory;
    private OSInfoDTO os;
    
    private List<ProcessInfoDTO> processes;
    private List<ProcessTreeDTO> processTree;
    
    private List<BatteryInfoDTO> batteries;
    private List<GPUInfoDTO> gpus;
    private List<DiskInfoDTO> disks;
    private List<NetworkInfoDTO> networks;
    private List<DBInfo> databases;
    private List<ProcessGroupDTO> groupedProcesses;
    private List<ProcessSectionDTO> processSections;
}
