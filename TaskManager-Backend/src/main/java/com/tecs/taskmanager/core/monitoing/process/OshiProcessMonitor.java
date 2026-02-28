package com.tecs.taskmanager.core.monitoing.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessSectionDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

@Component
public class OshiProcessMonitor implements ProcessMonitor {
    private final SystemInfo systemInfo;
    private final OperatingSystem os;
    private Map<Integer, OSProcess> previousSnapshot = new ConcurrentHashMap<>();

    public OshiProcessMonitor() {
        this.systemInfo = new SystemInfo();
        this.os = systemInfo.getOperatingSystem();
    }

    // Classification Engine
    private String classifyProcess(OSProcess process) {

        String osName = System.getProperty("os.name").toLowerCase();
        String name = process.getName() != null ? process.getName().toLowerCase() : "";
        String path = process.getPath() != null ? process.getPath().toLowerCase() : "";
        String user = process.getUser() != null ? process.getUser().toLowerCase() : "";
        int pid = process.getProcessID();

        if (osName.contains("win")) {

            if (pid == 0 || pid == 4) {
                return "SYSTEM_PROCESS";
            }

            if (name.equals("smss") ||
                    name.equals("csrss") ||
                    name.equals("wininit") ||
                    name.equals("winlogon") ||
                    name.equals("services") ||
                    name.equals("lsass")) {
                return "WINDOWS_PROCESS";
            }

            if (name.contains("svchost")) {
                return "WINDOWS_SERVICE";
            }

            if (path.contains("\\windows\\system32")) {
                return "WINDOWS_PROCESS";
            }

            if (process.getCommandLine() != null &&
                    process.getCommandLine().contains("--type=")) {

                return "BACKGROUND_PROCESS";
            }

            if (path.contains("\\program files") || path.contains("\\users")) {
                return "APPLICATION";
            }

            return "BACKGROUND_PROCESS";
        } else {

            if (pid == 1)
                return "SYSTEM_PROCESS";

            if (user.equals("root")) {
                return "SYSTEM_SERVICE";
            }

            if (path.startsWith("/usr") || path.startsWith("/bin") || path.startsWith("/sbin")) {
                return "SYSTEM_SERVICE";
            }

            if (path.startsWith("/home")) {
                return "APPLICATION";
            }

            return "BACKGROUND_PROCESS";
        }
    }

    @Override
    public List<ProcessInfoDTO> getAllProcesses() {
        List<OSProcess> processes = os.getProcesses();
        Map<Integer, OSProcess> currentSnapshot = new HashMap<>();

        List<ProcessInfoDTO> processList = processes.stream()
                .filter(p -> p.getProcessID() != 0)
                .map(process -> {
                    currentSnapshot.put(process.getProcessID(), process);
                    OSProcess previous = previousSnapshot.get(process.getProcessID());

                    if (previous != null && process.getStartTime() != previous.getStartTime()) {
                        previous = null;
                    }
                    double cpuLoad = process.getProcessCpuLoadBetweenTicks(previous) * 100.0
                            / systemInfo.getHardware().getProcessor().getLogicalProcessorCount();

                    double roundedCpu = Math.round(cpuLoad * 100.0) / 100.0;

                    return new ProcessInfoDTO(
                            process.getProcessID(),
                            process.getName(),
                            roundedCpu,
                            process.getResidentSetSize());
                }).collect(Collectors.toList());

        previousSnapshot.clear();
        previousSnapshot.putAll(currentSnapshot);
        return processList;
    }

    @Override
    public List<ProcessInfoDTO> getTopProcesses(int limit) {
        List<ProcessInfoDTO> all = getAllProcesses();
        return all.stream()
                .sorted((p1, p2) -> Double.compare(p2.getCpuLoad(), p1.getCpuLoad()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessInfoDTO> getProcessByName(String name) {
        return getAllProcesses().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    @Override
    public ProcessTreeDTO getProcessByPid(int pid) {
        OSProcess process = os.getProcess(pid);
        if (process == null)
            return null;

        return buildProcessTreeNode(process);
    }

    @Override
    public List<ProcessTreeDTO> getProcessTree() {
        List<OSProcess> processes = os.getProcesses();
        Map<Integer, ProcessTreeDTO> map = new HashMap<>();

        for (OSProcess process : processes) {
            if (process.getProcessID() == 0)
                continue;

            ProcessTreeDTO dto = buildProcessTreeNode(process);

            map.put(dto.getPid(), dto);
        }

        List<ProcessTreeDTO> roots = new ArrayList<>();
        for (ProcessTreeDTO process : map.values()) {
            if (map.containsKey(process.getParentId())) {
                map.get(process.getParentId())
                        .getChildren()
                        .add(process);
            } else {
                roots.add(process);
            }
        }
        for (ProcessTreeDTO root : roots) {
            aggregateUsage(root);
        }

        return roots;
    }

    private ProcessTreeDTO buildProcessTreeNode(OSProcess process) {
        double cpu = process.getProcessCpuLoadBetweenTicks(null) * 100.0
                / systemInfo.getHardware().getProcessor().getLogicalProcessorCount();

        double roundedCpu = Math.round(cpu * 100.0) / 100.0;

        long totalMemory = systemInfo.getHardware().getMemory().getTotal();

        double memoryPercent = (process.getResidentSetSize() * 100.0) / totalMemory;

        memoryPercent = Math.round(memoryPercent * 100.0) / 100.0;

        return ProcessTreeDTO.builder()
                .pid(process.getProcessID())
                .parentId(process.getParentProcessID())
                .name(process.getName())
                .commandLine(process.getCommandLine())
                .path(process.getPath())
                .user(process.getUser())
                .type(classifyProcess(process))
                .state(process.getState().name())
                .priority(process.getPriority())
                .cpuLoad(roundedCpu)
                .memory(process.getResidentSetSize())
                .virtualMemory(process.getVirtualSize())
                .memoryPercent(memoryPercent)
                .threads(process.getThreadCount())
                .starttime(process.getStartTime())
                .uptime(process.getUpTime())
                .bitness(process.getBitness())
                .children(new ArrayList<>())
                .build();
    }

    private void aggregateUsage(ProcessTreeDTO node) {
        for (ProcessTreeDTO child : node.getChildren()) {
            aggregateUsage(child);
            node.setCpuLoad(node.getCpuLoad() + child.getCpuLoad());
            node.setMemory(node.getMemory() + child.getMemory());
        }
    }

    @Override
    public List<ProcessGroupDTO> getGroupedProcesses() {
        List<ProcessTreeDTO> tree = getProcessTree();
        Map<String, ProcessGroupDTO> grouped = new HashMap<>();

        for (ProcessTreeDTO root : tree) {
            String key = root.getPath() != null && !root.getPath().isEmpty()
                    ? root.getPath()
                    : root.getName();

            grouped.computeIfAbsent(key, k -> new ProcessGroupDTO(
                    root.getName(),
                    root.getType(),
                    0,
                    0.0,
                    0L,
                    new ArrayList<>()));
            ProcessGroupDTO group = grouped.get(key);

            group.getInstances().add(root);
            group.setCount(group.getCount() + 1);
            group.setTotalCPU(group.getTotalCPU() + root.getCpuLoad());
            group.setTotalMemory(group.getTotalMemory() + root.getMemory());
        }
        return new ArrayList<>(grouped.values());
    }

    @Override
    public List<ProcessSectionDTO> getSectionProcesses() {
        List<ProcessGroupDTO> grouped = getGroupedProcesses();

        Map<String, List<ProcessGroupDTO>> sections = new HashMap<>();

        for (ProcessGroupDTO group : grouped) {
            String section;

            switch (group.getType()) {
                case "APPLICATION":
                    section = "Applications";
                    break;

                case "WINDOWS_PROCESS":
                case "WINDOWS_SERVICE":
                case "SYSTEM_PROCESS":
                    section = "Windows Processes";
                    break;
                case "SYSTEM_SERVICE":
                    section = "System Services";
                    break;
                default:
                    section = "Background Process";
            }
            sections.computeIfAbsent(section, k -> new ArrayList<>()).add(group);
        }
        List<ProcessSectionDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<ProcessGroupDTO>> entry : sections.entrySet()) {
            result.add(ProcessSectionDTO.builder()
                    .sectionName(entry.getKey())
                    .totalCount(entry.getValue().size())
                    .groups(entry.getValue())
                    .build());
        }
        return result;
    }
}
