package com.tecs.taskmanager.core.monitoing.process;

import java.util.List;

import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessKillPreviewDTO;
import com.tecs.taskmanager.dto.process.ProcessKillResponseDTO;
import com.tecs.taskmanager.dto.process.ProcessSectionDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;

public interface ProcessMonitor {
    List<ProcessInfoDTO> getTopProcesses(int limit);
    List<ProcessInfoDTO> getAllProcesses();
    List<ProcessTreeDTO> getProcessTree();
    ProcessTreeDTO getProcessByPid(int pid);
    List<ProcessInfoDTO> getProcessByName(String name);
    List<ProcessGroupDTO> getGroupedProcesses();
    List<ProcessSectionDTO> getSectionProcesses();
    ProcessKillResponseDTO killProcess(int pid, boolean force);
    ProcessKillPreviewDTO previewKillTree(int pid);
    ProcessKillResponseDTO killProcessTree(int pid, boolean force);
}
