package com.tecs.taskmanager.dto.gpu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GPUInfoDTO {
    private String deviceId;
    private String name;
    private String vendor;
    private String vram;
    private String versionInfo;
}
