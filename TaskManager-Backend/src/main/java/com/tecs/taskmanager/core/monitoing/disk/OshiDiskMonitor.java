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

@Component
public class OshiDiskMonitor implements DiskMonitor {

    private final SystemInfo systemInfo;
    private final Map<String, Long> prevRead = new ConcurrentHashMap<>();
    private final Map<String, Long> prevWrite = new ConcurrentHashMap<>();
    private final Map<String, Long> prevTime = new ConcurrentHashMap<>();

    public OshiDiskMonitor() {
        this.systemInfo = new SystemInfo();
    }

    @Override
    public List<DiskInfoDTO> getDiskInfo() {
        List<HWDiskStore> disks = systemInfo.getHardware().getDiskStores();

        return disks.stream().map(disk -> {
            disk.updateAttributes();

            String name = disk.getName();
            String model = disk.getModel();
            String serial = disk.getSerial();
            String size = String.format("%.1f GB", disk.getSize() / (1024.0 * 1024.0 * 1024.0));

            List<PartitionDTO> partitions = disk.getPartitions().stream()
                    .map(p -> new PartitionDTO(
                            p.getIdentification(),
                            p.getType(),
                            p.getUuid(),
                            (p.getSize() / (1024.0 * 1024.0 * 1024.0)) + " GB",
                            p.getMountPoint()))
                    .collect(Collectors.toList());

            long now = System.currentTimeMillis();

            long prevR = prevRead.getOrDefault(name, 0L);
            long prevW = prevWrite.getOrDefault(name, 0L);
            long prevT = prevTime.getOrDefault(name, now);

            long currR = disk.getReadBytes();
            long currW = disk.getWriteBytes();

            long elapsed = now - prevT;
            double seconds = elapsed / 1000.0;

            double readSpeedKBs = seconds > 0 ? (currR - prevR) / 1024.0 / seconds : 0;
            double writeSpeedKBs = seconds > 0 ? (currW - prevW) / 1024.0 / seconds : 0;

            prevRead.put(name, currR);
            prevWrite.put(name, currW);
            prevTime.put(name, now);

            String readSpeed = formatSpeed(readSpeedKBs);
            String writeSpeed = formatSpeed(writeSpeedKBs);

            return DiskInfoDTO.builder()
                    .name(name)
                    .model(model)
                    .serial(serial)
                    .size(size)
                    .partitions(partitions)
                    .readSpeed(readSpeed)
                    .writeSpeed(writeSpeed)
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
}
