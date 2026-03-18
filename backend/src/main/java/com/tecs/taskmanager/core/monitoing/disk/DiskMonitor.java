package com.tecs.taskmanager.core.monitoing.disk;

import java.util.List;

import com.tecs.taskmanager.dto.disk.DiskInfoDTO;

public interface DiskMonitor {
    List<DiskInfoDTO> getDiskInfo();
}
