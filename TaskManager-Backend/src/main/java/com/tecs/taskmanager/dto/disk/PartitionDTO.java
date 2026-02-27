package com.tecs.taskmanager.dto.disk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartitionDTO {

    private String identification;
    private String type;
    private String uuid;
    private String size;
    private String mountPoint;
}
