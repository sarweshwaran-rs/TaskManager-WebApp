package com.tecs.taskmanager.dto.cpu;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpuInfoDTO {
    // Dynamic
    private double systemLoad;
    private List<Double> perCoreLoad;
    private long processes;
    private long threads;
    private Object handles;
    private long interrupts;
    private String temperature;
    private String voltage;
    private String frequency;
    private String uptime;

    // Static
    private String name;
    private String vendor;
    private int cores;
    private int logicalCores;
    private String processorFamily;
    private String processorModel;
    private String processorId;
    private String microarchitecture;
    private long vendorFrequency;

}
