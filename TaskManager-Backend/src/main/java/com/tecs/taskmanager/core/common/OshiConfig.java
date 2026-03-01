package com.tecs.taskmanager.core.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.Sensors;
import oshi.hardware.VirtualMemory;
import oshi.software.os.OperatingSystem;

@Configuration
public class OshiConfig {

    @Bean
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }

    @Bean
    public OperatingSystem operatingSystem(SystemInfo si) {
        return si.getOperatingSystem();
    }

    @Bean
    public CentralProcessor processor(SystemInfo si) {
        return si.getHardware().getProcessor();
    }

    @Bean
    public Sensors sensors(SystemInfo si) {
        return si.getHardware().getSensors();
    }

    @Bean
    public GlobalMemory globalMemory(SystemInfo si) {
        return si.getHardware().getMemory();
    }

    @Bean
    public VirtualMemory virtualMemory(GlobalMemory globalMemory) {
        return globalMemory.getVirtualMemory();
    }
    @Bean
    public ComputerSystem computerSystem(SystemInfo si) {
        return si.getHardware().getComputerSystem();
    }

    @Bean
    public Baseboard baseboard(SystemInfo si) {
        return si.getHardware().getComputerSystem().getBaseboard();
    }
}
