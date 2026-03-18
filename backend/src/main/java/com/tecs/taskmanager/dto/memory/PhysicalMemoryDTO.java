package com.tecs.taskmanager.dto.memory;

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
public class PhysicalMemoryDTO {
    private String bankLabel;
    private double capacityGB;
    private long clockSpeed;
    private String manufacturer;
    private String memoryType;
}
