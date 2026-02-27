package com.tecs.taskmanager.core.monitoing.process;

import java.util.List;

import com.tecs.taskmanager.dto.process.ProcessInfoDTO;

public interface ProcessMonitor {
    List<ProcessInfoDTO> getTopProcesses(int limit);
}
