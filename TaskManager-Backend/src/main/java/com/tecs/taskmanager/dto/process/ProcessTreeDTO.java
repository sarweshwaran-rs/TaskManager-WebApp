package com.tecs.taskmanager.dto.process;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
    
    private String state;
    private int priority;
    
    private double cpuLoad;
    private double memoryPercent;
    
    private long memory;
    private long virtualMemory;
    private long threads;
    
    private long starttime;
    private long uptime;
    
    private int bitness;
    private List<ProcessTreeDTO> children;
}
