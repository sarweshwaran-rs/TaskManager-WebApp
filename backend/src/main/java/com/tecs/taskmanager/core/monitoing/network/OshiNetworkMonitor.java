package com.tecs.taskmanager.core.monitoing.network;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.network.NetworkInfoDTO;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import oshi.hardware.NetworkIF.IfOperStatus;

@Component
public class OshiNetworkMonitor implements NetworkMonitor {

    private final SystemInfo systemInfo;

    // Thread-safe previous counters
    private final Map<String, Long> prevBytesSent = new ConcurrentHashMap<>();
    private final Map<String, Long> prevBytesRecv = new ConcurrentHashMap<>();
    private final Map<String, Long> prevTime = new ConcurrentHashMap<>();

    public OshiNetworkMonitor(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<NetworkInfoDTO> getNetworkInfo() {

        List<NetworkIF> networks = systemInfo.getHardware().getNetworkIFs();
        long now = System.currentTimeMillis();

        return networks.stream()
                .filter(network -> network.getIfOperStatus() == IfOperStatus.UP
                        && !network.isKnownVmMacAddr()
                        && network.getIPv4addr().length > 0
                        && !network.getDisplayName().toLowerCase().contains("virtual"))
                .map(network -> {

                    String ifName = network.getName();

                    long bytesSent = network.getBytesSent();
                    long bytesReceived = network.getBytesRecv();

                    long prevSent = prevBytesSent.getOrDefault(ifName, bytesSent);
                    long prevRecv = prevBytesRecv.getOrDefault(ifName, bytesReceived);
                    long prevT = prevTime.getOrDefault(ifName, now);

                    double sentMbps = 0.0;
                    double recvMbps = 0.0;

                    double timeDiffSec = (now - prevT) / 1000.0;

                    if (timeDiffSec > 0) {
                        sentMbps = (bytesSent - prevSent) * 8
                                / (1000.0 * 1000.0 * timeDiffSec);

                        recvMbps = (bytesReceived - prevRecv) * 8
                                / (1000.0 * 1000.0 * timeDiffSec);
                    }

                    // update previous values
                    prevBytesSent.put(ifName, bytesSent);
                    prevBytesRecv.put(ifName, bytesReceived);
                    prevTime.put(ifName, now);

                    return NetworkInfoDTO.builder()
                            .name(network.getName())
                            .displayName(network.getDisplayName())
                            .macAddress(network.getMacaddr())
                            .mtu((int) network.getMTU())
                            .speed((network.getSpeed() / 1_000_000) + " mb/s")
                            .totalBytesSent(bytesSent)
                            .totalBytesReceived(bytesReceived)
                            .ipV4(formatIPv4(network.getIPv4addr()))
                            .ipV6(getPrimaryIpv6Address(network.getIPv6addr()))
                            .subnetMask(formatSubnetMasks(network.getSubnetMasks()))
                            .uploadSpeed(String.format("%.2f mb/s", sentMbps))
                            .downloadSpeed(String.format("%.2f mb/s", recvMbps))
                            .connectionType(resolveConnectionType(network.getName()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String formatIPv4(String[] ipv4) {
        return Arrays.toString(ipv4)
                .replace("[", "")
                .replace("]", "");
    }

    private String getPrimaryIpv6Address(String[] ipv6Addresses) {
        if (ipv6Addresses == null || ipv6Addresses.length == 0) {
            return "";
        }
        for (String ip : ipv6Addresses) {
            if (ip != null && !ip.toLowerCase().startsWith("fe80")) {
                return ip;
            }
        }
        return ipv6Addresses[0];
    }

    private String formatSubnetMasks(Short[] prefixes) {
        if (prefixes == null || prefixes.length == 0) {
            return "";
        }

        return Arrays.stream(prefixes)
                .filter(p -> p != null)
                .map(prefix -> {
                    int mask = 0xffffffff << (32 - prefix);
                    return String.format("%d.%d.%d.%d",
                            (mask >> 24) & 0xff,
                            (mask >> 16) & 0xff,
                            (mask >> 8) & 0xff,
                            mask & 0xff);
                })
                .collect(Collectors.joining(", "));
    }

    private String resolveConnectionType(String name) {

        String lower = name.toLowerCase();

        if (lower.contains("eth") || lower.contains("eno")) {
            return "Ethernet";
        } else if (lower.contains("wifi")
                || lower.contains("wlan")
                || lower.contains("wireless")) {
            return "Wi-Fi";
        } else if (lower.contains("bluetooth")) {
            return "Bluetooth";
        }

        return "Unknown";
    }
}
