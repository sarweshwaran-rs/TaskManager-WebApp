package com.tecs.taskmanager.dto.os;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OSInfoDTO {

    private String family;
    private String version;
    private String mmanufacturer;
}
