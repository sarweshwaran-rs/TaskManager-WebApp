package com.tecs.taskmanager.TaskManager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.hardware.NetworkIF.IfOperStatus;
import oshi.hardware.Sensors;
import oshi.hardware.VirtualMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.hardware.Baseboard;

@Service
public class SystemMonitorService {
    private final SystemInfo systeminfo = new SystemInfo();
    private final OperatingSystem os = systeminfo.getOperatingSystem();
    private final CentralProcessor processor = systeminfo.getHardware().getProcessor();
    private final Sensors sensors = systeminfo.getHardware().getSensors();
    private long[] prevTicks = processor.getSystemCpuLoadTicks();
    private long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
    private final Map<String, Long> prevBytesSent = new HashMap<>();
    private final Map<String, Long> prevBytesRecv = new HashMap<>();
    private final Map<String, Long> prevTimeStaps = new HashMap<>();
    
    // For calculating per-process CPU load between ticks
    private Map<Integer, OSProcess> priorSnapshot = new HashMap<>();

    public Map<String, Object> getCpuInfo() {
        Map<String, Object> data = new HashMap<>();

        // --- Continuously Changing Data for Graphing ---
        double totalLoadPercent = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();

        data.put("systemLoad", totalLoadPercent);

        double[] processorLoad = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        prevProcTicks = processor.getProcessorCpuLoadTicks();
        List<String> perProcessorLoad = new ArrayList<>();
        for (double load : processorLoad) {
            perProcessorLoad.add(String.format("%.2f", load * 100));
        }
        data.put("perProcessorLoad", perProcessorLoad);

        List<OSProcess> processList = os.getProcesses();
        long totalThreads = 0;
        long totalHandles = 0;
        for (OSProcess p : processList) {
            totalThreads += p.getThreadCount();
            totalHandles += p.getOpenFiles();
        }
        data.put("Processes", processList.size());
        data.put("Threads", totalThreads);
        data.put("Handles", totalHandles > 0 ? totalHandles : "N/A");
        data.put("Interrupts", processor.getInterrupts()); // This is cumulative but changes rapidly

        double temp = sensors.getCpuTemperature();
        double voltage = sensors.getCpuVoltage();
        data.put("Temperature", temp > 0 ? String.format("%.1f °C", temp) : "N/A");
        data.put("Voltage", voltage > 0 ? String.format("%.2f V", voltage) : "N/A");

        long freq = processor.getMaxFreq();
        data.put("frequency", freq > 0 ? String.format("%.2f GHz", freq / 1_000_000_000.0) : "N/A");

        // --- Static CPU Information ---
        data.put("name", processor.getProcessorIdentifier().getName());
        data.put("vendor", processor.getProcessorIdentifier().getVendor());
        data.put("cores", processor.getPhysicalProcessorCount());
        data.put("logicalCores", processor.getLogicalProcessorCount());
        data.put("Processor Family", processor.getProcessorIdentifier().getFamily());
        data.put("Processor Model", processor.getProcessorIdentifier().getModel());
        data.put("Processor Stepping", processor.getProcessorIdentifier().getStepping());
        data.put("processorId", processor.getProcessorIdentifier().getProcessorID());
        data.put("microarchitecture", processor.getProcessorIdentifier().getMicroarchitecture());
        data.put("Vendor Frequency", processor.getProcessorIdentifier().getVendorFreq());

        long uptimeSeconds = os.getSystemUptime();
        long days = uptimeSeconds / (24 * 3600);
        long hours = (uptimeSeconds % (24 * 3600)) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        String formattedUptime = String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        data.put("Uptime", formattedUptime);

        return data;
    }

    public Map<String, Object> getMemoryInfo() {
        GlobalMemory memory = systeminfo.getHardware().getMemory();
        VirtualMemory virtualMemory = memory.getVirtualMemory();
        Map<String, Object> data = new HashMap<>();
        
        // Send values in GB as numbers for better frontend flexibility
        double totalGB = memory.getTotal() / (1024.0 * 1024.0 * 1024.0);
        double availableGB = memory.getAvailable() / (1024.0 * 1024.0 * 1024.0);
        double usedGB = totalGB - availableGB;

        double swapTotalGB = virtualMemory.getSwapTotal() / (1024.0 * 1024.0 * 1024.0);
        double swapUsedGB = virtualMemory.getSwapUsed() / (1024.0 * 1024.0 * 1024.0);


        data.put("totalGB", totalGB);
        data.put("availableGB", availableGB);
        data.put("usedGB", usedGB);

        data.put("swapTotalGB", swapTotalGB);
        data.put("swapUsedGB", swapUsedGB);

        data.put("Physical Memory", memory.getPhysicalMemory());
        return data;
    }

    public Map<String, Object> getOsInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("family", os.getFamily());
        data.put("version", os.getVersionInfo().toString());
        data.put("Manufacturer", os.getManufacturer());
        return data;
    }

    public List<Map<String, Object>> getProcesses(int limit) {
        List<OSProcess> processes = os.getProcesses();
        Map<Integer, OSProcess> currentSnapshot = new HashMap<>();

        List<Map<String, Object>> processList = new ArrayList<>();
        for (OSProcess p : processes) {
            currentSnapshot.put(p.getProcessID(), p);
            OSProcess priorProc = priorSnapshot.get(p.getProcessID());

            // if priorProc is not null, check if start time is the same. If not, it's a new process with a reused PID
            if (priorProc != null && p.getStartTime() != priorProc.getStartTime()) {
                priorProc = null;
            }

            // getProcessCpuLoadBetweenTicks provides a value between 0.0 and 1.0
            double cpuLoad = p.getProcessCpuLoadBetweenTicks(priorProc) * 100.0;
            
            double roundedCpuLoad = Math.round(cpuLoad * 100.0) / 100.0;

            Map<String, Object> procData = new HashMap<>();
            procData.put("pid", p.getProcessID());
            procData.put("name", p.getName());
            procData.put("cpuLoad", roundedCpuLoad);
            procData.put("memory", p.getResidentSetSize());
            processList.add(procData);
        }
        
        priorSnapshot = currentSnapshot;

        // Sort by CPU load in descending order
        processList.sort((p1, p2) -> Double.compare((double) p2.get("cpuLoad"), (double) p1.get("cpuLoad")));

        return processList.stream().limit(limit).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getGpuInfo() {
        List<GraphicsCard> gpus = systeminfo.getHardware().getGraphicsCards();

        return gpus.stream().map(gpu -> {
            Map<String, Object> data = new HashMap<>();
            data.put("deviceId", gpu.getDeviceId());
            data.put("name", gpu.getName());
            data.put("vendor", gpu.getVendor());
            double vramGB = gpu.getVRam() / (1024.0 * 1024.0 * 1024.0);
            data.put("vram", String.format("%.1f GB", vramGB));
            data.put("versionInfo", gpu.getVersionInfo());
            return data;
        }).collect(Collectors.toList());
    }

    // Keep previous counters
    private final Map<String, Long> prevRead = new HashMap<>();
    private final Map<String, Long> prevWrite = new HashMap<>();
    private final Map<String, Long> prevTime = new HashMap<>();

    public List<Map<String, Object>> getDiskInfo() {
        List<HWDiskStore> disks = systeminfo.getHardware().getDiskStores();

        return disks.stream().map(disk -> {
            disk.updateAttributes(); // refresh I/O stats
            Map<String, Object> data = new HashMap<>();
            data.put("name", disk.getName());
            data.put("model", disk.getModel());
            data.put("serial", disk.getSerial());
            data.put("size", String.format("%.1f GB", disk.getSize() / (1024.0 * 1024.0 * 1024.0)));

            // Partition info
            data.put("Partitions", disk.getPartitions().stream().map(partition -> {
                Map<String, Object> pdata = new HashMap<>();
                pdata.put("identification", partition.getIdentification());
                pdata.put("type", partition.getType());
                pdata.put("uuid", partition.getUuid());
                pdata.put("size", (partition.getSize() / (1024 * 1024 * 1024)) + " GB");
                pdata.put("mountPoint", partition.getMountPoint());
                return pdata;
            }).collect(Collectors.toList()));

            // Time now
            long now = System.currentTimeMillis();

            // Previous values
            long prevR = prevRead.getOrDefault(disk.getName(), 0L);
            long prevW = prevWrite.getOrDefault(disk.getName(), 0L);
            long prevT = prevTime.getOrDefault(disk.getName(), now);

            // Current values
            long currR = disk.getReadBytes();
            long currW = disk.getWriteBytes();

            long elapsed = now - prevT; // in ms
            double seconds = elapsed / 1000.0;

            double readSpeedKBs = seconds > 0 ? (currR - prevR) / 1024.0 / seconds : 0;
            double writeSpeedKBs = seconds > 0 ? (currW - prevW) / 1024.0 / seconds : 0;

            // Store back for next calculation
            prevRead.put(disk.getName(), currR);
            prevWrite.put(disk.getName(), currW);
            prevTime.put(disk.getName(), now);
            String readSpeed, writeSpeed;
            
            if(readSpeedKBs >= 1024) {
                readSpeed = String.format("%.2f MB/s", readSpeedKBs / 1024.0);
            } else {
                readSpeed = String.format("%.2f kB/s", readSpeedKBs);
            }

            if(writeSpeedKBs >= 1024) {
                writeSpeed = String.format("%.2f MB/s", writeSpeedKBs / 1024.0);
            } else {
                writeSpeed = String.format("%.2f kB/s", writeSpeedKBs);
            }
            // Format as KB/s
            data.put("readSpeed", readSpeed);
            data.put("writeSpeed", writeSpeed);

            return data;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getNetworkInfo() {
        List<NetworkIF> networks = systeminfo.getHardware().getNetworkIFs();
        long now = System.currentTimeMillis();
        return networks.stream()
                .filter(network -> network.getIfOperStatus() == IfOperStatus.UP &&
                        !network.isKnownVmMacAddr() &&
                        network.getIPv4addr().length > 0 &&
                        !network.getDisplayName().toLowerCase().contains("virtual"))
                .map(network -> {
                    Map<String, Object> data = new HashMap<>();
                    String ifName = network.getName();

                    long bytesSent = network.getBytesSent();
                    long bytesReceived = network.getBytesRecv();

                    long prevSent = prevBytesSent.getOrDefault(ifName, bytesSent);
                    long prevRecv = prevBytesRecv.getOrDefault(ifName, bytesReceived);
                    long prevTime = prevTimeStaps.getOrDefault(ifName, now);

                    double sentMbps = 0.0;
                    double recvMbps = 0.0;

                    if (prevTime != now) {
                        double timeDiffsec = (now - prevTime) / 1000.0;

                        if (timeDiffsec > 0) {
                            sentMbps = (bytesSent - prevSent) * 8 / (1000.0 * 1000.0 * timeDiffsec);
                            recvMbps = (bytesReceived - prevRecv) * 8 / (1000.0 * 1000.0 * timeDiffsec);
                        }
                    }

                    prevBytesSent.put(ifName, bytesSent);
                    prevBytesRecv.put(ifName, bytesReceived);
                    prevTimeStaps.put(ifName, now);

                    data.put("name", network.getName());
                    data.put("displayName", network.getDisplayName());
                    data.put("macAddress", network.getMacaddr());
                    data.put("mtu", network.getMTU());
                    data.put("speed", (network.getSpeed() / 1_000_000) + " mb/s");
                    data.put("totalBytesSent", network.getBytesSent());
                    data.put("totalBytesReceived", network.getBytesRecv());
                    data.put("ipv4", Arrays.toString(network.getIPv4addr()).replace("[", "").replace("]", ""));
                    data.put("ipv6", getPrimaryIpv6Address(network.getIPv6addr()));
                    data.put("subnetMask", formatSubnetMasks(network.getSubnetMasks()));
                    data.put("uploadSpeed", String.format("%.2f mb/s", sentMbps));
                    data.put("downloadSpeed", String.format("%.2f mb/s", recvMbps));

                    String type = "Unknown";
                    String name = network.getName().toLowerCase();
                    if (name.contains("eth") || name.contains("eno1")) {
                        type = "Ethernet";
                    } else if (name.contains("wifi") || name.contains("wlan") || name.contains("Wi-fi")
                            || name.contains("wireless") || name.contains("wlx002e2d0081b0")) {
                        type = "Wi-Fi";
                    } else if (name.contains("bluetooth")) {
                        type = "Bluetooth";
                    }

                    data.put("connectionType", type);
                    return data;
                }).collect(Collectors.toList());
    }

    private String getPrimaryIpv6Address(String[] ipv6Addresses) {
        if (ipv6Addresses == null || ipv6Addresses.length == 0) {
            return "";
        }
        // Prefer a global address over a link-local one
        for (String ip : ipv6Addresses) {
            if (ip != null && !ip.toLowerCase().startsWith("fe80")) {
                return ip; // Found a global one, return it
            }
        }
        // If no global address is found, fall back to the first address in the list
        return ipv6Addresses[0];
    }

    private String formatSubnetMasks(Short[] prefixes) {
        if (prefixes == null || prefixes.length == 0) {
            return "";
        }
        List<String> masks = new ArrayList<>();
        for (Short prefix : prefixes) {
            if (prefix != null) {
                try {
                    int mask = 0xffffffff << (32 - prefix);
                    int[] bytes = new int[4];
                    bytes[0] = (mask >> 24) & 0xff;
                    bytes[1] = (mask >> 16) & 0xff;
                    bytes[2] = (mask >> 8) & 0xff;
                    bytes[3] = mask & 0xff;
                    masks.add(String.format("%d.%d.%d.%d", bytes[0], bytes[1], bytes[2], bytes[3]));
                } catch (Exception e) {
                    // Ignore invalid prefix
                }
            }
        }
        return Arrays.toString(masks.toArray()).replace("[", "").replace("]", "");
    }

    public Map<String, Object> getComputerInfo() {
        Map<String, Object> data = new HashMap<>();
        Baseboard baseboard = systeminfo.getHardware().getComputerSystem().getBaseboard();
        ComputerSystem cs = systeminfo.getHardware().getComputerSystem();
        data.put("Manufacturer", baseboard.getManufacturer());
        data.put("Baseboard Model", baseboard.getModel());
        data.put("System Model", cs.getModel());
        data.put("Version Number", baseboard.getVersion());
        data.put("Firmware", cs.getFirmware());
        data.put("Firmware Version", cs.getFirmware().getVersion());
        data.put("Firmware Description", cs.getFirmware().getDescription());
        data.put("Firmware Release Date", cs.getFirmware().getReleaseDate());
        return data;
    }
}