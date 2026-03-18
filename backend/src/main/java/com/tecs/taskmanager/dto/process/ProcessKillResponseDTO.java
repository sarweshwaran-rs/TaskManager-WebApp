package com.tecs.taskmanager.dto.process;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessKillResponseDTO {

    private int pid;
    private boolean success;
    private String message;
}
