package com.tecs.taskmanager.dto.process;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessGroupDTO {
    private String name;
    private String type;
    private int count;
    
    private double totalCPU;
    private long totalMemory;

    private List<ProcessTreeDTO> instances;
}
