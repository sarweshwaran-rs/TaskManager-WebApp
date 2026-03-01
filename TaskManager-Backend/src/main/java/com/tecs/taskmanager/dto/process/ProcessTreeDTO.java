package com.tecs.taskmanager.dto.process;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTreeDTO {
    private int pid;
    private int parentId;
    
    private String name;
    private String commandLine;
    private String path;
    private String user;
    private String type;

    private String state;
    private int priority;
    
    private double cpuLoad;
    private double memoryPercent;
    
    private long memory;
    private String FMemory;
    private long virtualMemory;
    private String FVirtualMemory;
    private long threads;
    
    private long starttime;
    private String FStarttime;
    private long uptime;
    private String FUptime;
    
    private int bitness;
    private List<ProcessTreeDTO> children;
}
