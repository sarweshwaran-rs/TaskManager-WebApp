package com.tecs.taskmanager.dto.process;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessKillPreviewDTO {
    private int rootPid;
    private String rootName;
    private List<Integer> affectedPids;
    private int totalProcesses;
}
