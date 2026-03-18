package com.tecs.taskmanager.dto.process;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProcessSectionDTO {
    private String sectionName;
    private int totalCount;
    private List<ProcessGroupDTO> groups;
}
