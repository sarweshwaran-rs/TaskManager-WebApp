package com.tecs.taskmanager.core.monitoing.disk;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.disk.DiskInfoDTO;
import com.tecs.taskmanager.dto.disk.PartitionDTO;

import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.software.os.OSFileStore;

@Component
public class OshiDiskMonitor implements DiskMonitor {

    private final SystemInfo systemInfo;
    private final Map<String, Long> prevRead = new ConcurrentHashMap<>();
    private final Map<String, Long> prevWrite = new ConcurrentHashMap<>();
    private final Map<String, Long> prevTime = new ConcurrentHashMap<>();

    public OshiDiskMonitor(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<DiskInfoDTO> getDiskInfo() {
        List<HWDiskStore> disks = systemInfo.getHardware().getDiskStores();
        List<OSFileStore> fileStores = systemInfo.getOperatingSystem().getFileSystem().getFileStores();

        return disks.stream().map(disk -> {
            disk.updateAttributes();

            String name = disk.getName();
            String model = disk.getModel();
            String serial = disk.getSerial();
            String size = String.format("%.1f GB",
                    disk.getSize() / (1024.0 * 1024.0 * 1024.0));

            List<PartitionDTO> partitions = disk.getPartitions().stream()
                    .map(partition -> {

                        OSFileStore matchedFs = fileStores.stream()
                                .filter(fs -> fs.getMount()
                                        .equalsIgnoreCase(partition.getMountPoint()))
                                .findFirst()
                                .orElse(null);

                        double totalGB = 0;
                        double usedGB = 0;
                        double usagePercent = 0;

                        if (matchedFs != null) {

                            long total = matchedFs.getTotalSpace();
                            long usable = matchedFs.getUsableSpace();
                            long used = total - usable;

                            totalGB = round(total / (1024.0 * 1024.0 * 1024.0));
                            usedGB = round(used / (1024.0 * 1024.0 * 1024.0));

                            if (totalGB > 0) {
                                usagePercent = round((usedGB * 100.0) / totalGB);
                            }
                        }

                        return new PartitionDTO(
                                partition.getIdentification(),
                                partition.getType(),
                                partition.getUuid(),
                                String.format("%.2f GB",
                                        partition.getSize() / (1024.0 * 1024.0 * 1024.0)),
                                partition.getMountPoint(),
                                totalGB,
                                usedGB,
                                usagePercent);
                    })
                    .collect(Collectors.toList());

            long now = System.currentTimeMillis();

            long prevR = prevRead.getOrDefault(name, 0L);
            long prevW = prevWrite.getOrDefault(name, 0L);
            long prevT = prevTime.getOrDefault(name, now);

            long currR = disk.getReadBytes();
            long currW = disk.getWriteBytes();

            long elapsed = now - prevT;
            double seconds = elapsed / 1000.0;

            double readSpeedKBs = seconds > 0
                    ? (currR - prevR) / 1024.0 / seconds
                    : 0;

            double writeSpeedKBs = seconds > 0
                    ? (currW - prevW) / 1024.0 / seconds
                    : 0;

            prevRead.put(name, currR);
            prevWrite.put(name, currW);
            prevTime.put(name, now);

            return DiskInfoDTO.builder()
                    .name(name)
                    .model(model)
                    .serial(serial)
                    .size(size)
                    .partitions(partitions)
                    .readSpeed(formatSpeed(readSpeedKBs))
                    .writeSpeed(formatSpeed(writeSpeedKBs))
                    .build();
        }).collect(Collectors.toList());
    }

    private String formatSpeed(double speedKBs) {
        if (speedKBs >= 1024) {
            return String.format("%.2f MB/s", speedKBs / 1024.0);
        } else {
            return String.format("%.2f KB/s", speedKBs);
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
