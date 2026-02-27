package com.tecs.taskmanager.core.monitoing.process;

import java.util.List;

import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;

public interface ProcessMonitor {
    List<ProcessInfoDTO> getTopProcesses(int limit);
    List<ProcessInfoDTO> getAllProcesses();
    List<ProcessTreeDTO> getProcessTree();
    ProcessTreeDTO getProcessByPid(int pid);
    List<ProcessInfoDTO> getProcessByName(String name);
}
