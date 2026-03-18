package com.tecs.taskmanager.dto.disk;

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
public class DiskInfoDTO {

    private String name;
    private String model;
    private String serial;
    private String size;

    private List<PartitionDTO> partitions;

    private String readSpeed;
    private String writeSpeed;
}
