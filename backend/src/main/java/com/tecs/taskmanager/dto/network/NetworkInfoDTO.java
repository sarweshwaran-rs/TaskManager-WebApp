package com.tecs.taskmanager.dto.network;
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
public class NetworkInfoDTO {
    private String name;
    private String displayName;
    private String macAddress;
    private int mtu;
    private String speed;

    private long totalBytesSent;
    private long totalBytesReceived;

    private String ipV4;
    private String ipV6;
    private String subnetMask;

    private String uploadSpeed;
    private String downloadSpeed;

    private String connectionType;
}
