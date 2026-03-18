package com.tecs.taskmanager.core.monitoing.os;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.os.ComputerInfoDTO;
import com.tecs.taskmanager.dto.os.OSInfoDTO;

import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.software.os.OperatingSystem;

@Component
public class OshiOSMonitor implements OSMonitor {
    private final OperatingSystem os;
    private final Baseboard baseboard;
    private final ComputerSystem cs;

    public OshiOSMonitor(OperatingSystem operatingSystem, Baseboard baseboard, ComputerSystem computerSystem) {
        this.os = operatingSystem;
        this.baseboard = baseboard;
        this.cs = computerSystem;
    }

    @Override
    public OSInfoDTO getOSInfo() {
        return OSInfoDTO.builder()
                .family(os.getFamily())
                .version(os.getVersionInfo().toString())
                .mmanufacturer(os.getManufacturer())
                .build();
    }

    @Override
    public ComputerInfoDTO getComputerInfo() {
        return ComputerInfoDTO.builder()
        .manufacturer(baseboard.getManufacturer())
        .baseboardModel(baseboard.getModel())
        .baseboardVersion(baseboard.getVersion())
        .systemModel(cs.getModel())
        .firmwareName(cs.getFirmware().getName())
        .firmwareDescription(cs.getFirmware().getDescription())
        .firmwareVersion(cs.getFirmware().getVersion())
        .firmwareReleaseDate(cs.getFirmware().getReleaseDate())
        .build();
    }
}
