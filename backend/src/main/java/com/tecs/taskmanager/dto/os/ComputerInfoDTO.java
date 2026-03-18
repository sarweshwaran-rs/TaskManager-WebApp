package com.tecs.taskmanager.dto.os;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerInfoDTO {
    private String manufacturer;
    private String baseboardModel;
    private String baseboardVersion;
    private String systemModel;
    private String firmwareName;
    private String firmwareDescription;
    private String firmwareVersion;
    private String firmwareReleaseDate;
}
