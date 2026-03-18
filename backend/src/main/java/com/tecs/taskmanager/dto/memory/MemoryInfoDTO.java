package com.tecs.taskmanager.dto.memory;
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
public class MemoryInfoDTO {
    
    private double totalGB;
    private double availableGB;
    private double usedGB;

    private double swapTotalGB;
    private double swapUsedGB;
    private double usagePercent;
    
    private List<PhysicalMemoryDTO> physicalMemory;
}
