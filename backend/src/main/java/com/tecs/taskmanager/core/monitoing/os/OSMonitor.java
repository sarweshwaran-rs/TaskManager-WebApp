package com.tecs.taskmanager.core.monitoing.os;

import com.tecs.taskmanager.dto.os.ComputerInfoDTO;
import com.tecs.taskmanager.dto.os.OSInfoDTO;

public interface OSMonitor {
    OSInfoDTO getOSInfo();
    ComputerInfoDTO getComputerInfo();
}
