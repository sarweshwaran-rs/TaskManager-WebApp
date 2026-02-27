package com.tecs.taskmanager.core.monitoing.network;

import java.util.List;

import com.tecs.taskmanager.dto.network.NetworkInfoDTO;

public interface NetworkMonitor {
    List<NetworkInfoDTO> getNetworkInfo();
}
