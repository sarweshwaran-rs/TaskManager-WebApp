package com.tecs.taskmanager.core.monitoing.cpu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.cpu.CpuInfoDTO;

import oshi.hardware.CentralProcessor;
import oshi.hardware.Sensors;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

@Component
public class OshiCPUMonitor implements CPUMonitor {

        private final CentralProcessor processor;
        private final Sensors sensors;
        private final OperatingSystem os;

        private long[] prevTicks;
        private long[][] prevProcTicks;

        public OshiCPUMonitor(CentralProcessor processor, Sensors sensors, OperatingSystem os) {
                this.processor = processor;
                this.sensors = sensors;
                this.os = os;

                this.prevTicks = processor.getSystemCpuLoadTicks();
                this.prevProcTicks = processor.getProcessorCpuLoadTicks();
        }

        @Override
        public CpuInfoDTO getCpuInfo() {

                // ================= SYSTEM LOAD =================
                double totalLoadPercent = Math.round(processor.getSystemCpuLoadBetweenTicks(prevTicks) * 10000.0)
                                / 100.0;

                prevTicks = processor.getSystemCpuLoadTicks();

                // ================= PER CORE LOAD =================
                double[] processorLoad = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);

                prevProcTicks = processor.getProcessorCpuLoadTicks();

                List<Double> perCoreLoad = new ArrayList<>();
                for (double load : processorLoad) {
                        perCoreLoad.add(
                                        Math.round(load * 10000.0) / 100.0);
                }

                List<OSProcess> processList = os.getProcesses();

                long totalThreads = 0;
                long totalHandles = 0;

                for (OSProcess p : processList) {
                        totalThreads += p.getThreadCount();
                        totalHandles += p.getOpenFiles();
                }

                int totalProcesses = processList.size();

                // ================= UPTIME =================
                long uptimeSeconds = os.getSystemUptime();
                String uptime = formatUptime(uptimeSeconds);

                return CpuInfoDTO.builder()
                                .systemLoad(totalLoadPercent)
                                .perCoreLoad(perCoreLoad)
                                .processes(totalProcesses)
                                .threads(totalThreads)
                                .handles(totalHandles > 0 ? totalHandles : null)
                                .interrupts(processor.getInterrupts())
                                .temperature("N/A")
                                .voltage("N/A")
                                .frequency(processor.getMaxFreq() > 0 ? String.format("%.2f GHz",processor.getMaxFreq() / 1_000_000_000.0) : "N/A")
                                .uptime(uptime)
                                .name(processor.getProcessorIdentifier().getName())
                                .vendor(processor.getProcessorIdentifier().getVendor())
                                .cores(processor.getPhysicalProcessorCount())
                                .logicalCores(processor.getLogicalProcessorCount())
                                .processorId(processor.getProcessorIdentifier().getProcessorID())
                                .processorFamily(processor.getProcessorIdentifier().getFamily())
                                .processorModel(processor.getProcessorIdentifier().getModel())
                                .microarchitecture(processor.getProcessorIdentifier().getMicroarchitecture())
                                .vendorFrequency(processor.getProcessorIdentifier().getVendorFreq())
                                .build();
        }

        private String formatUptime(long seconds) {
                long days = seconds / (24 * 3600);
                long hours = (seconds % (24 * 3600)) / 3600;
                long minutes = (seconds % 3600) / 60;
                long sec = seconds % 60;
                return String.format("%d:%02d:%02d:%02d", days, hours, minutes, sec);
        }
}