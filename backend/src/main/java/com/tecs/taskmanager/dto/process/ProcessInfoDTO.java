package com.tecs.taskmanager.dto.process;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfoDTO {
    private int pid;
    private String name;
    private double cpuLoad;
    private long memory;
}
