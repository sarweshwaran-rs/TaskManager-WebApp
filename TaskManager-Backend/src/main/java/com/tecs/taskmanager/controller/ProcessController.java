package com.tecs.taskmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tecs.taskmanager.core.monitoing.process.ProcessMonitor;
import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessSectionDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;

@RestController
@RequestMapping("/api/processes")
public class ProcessController {
    private final ProcessMonitor processMonitor;

    public ProcessController(ProcessMonitor processMonitor) {
        this.processMonitor = processMonitor;
    }
    @GetMapping
    public List<ProcessInfoDTO> getAllProcesses() {
        return processMonitor.getAllProcesses();
    }

    @GetMapping("/top")
    public List<ProcessInfoDTO> getTopProcesses(@RequestParam(defaultValue = "10") int limit) {
        return processMonitor.getTopProcesses(limit);
    }

    @GetMapping("/{pid}")
    public ProcessTreeDTO getProcessByPid(@PathVariable int pid) {
        ProcessTreeDTO process = processMonitor.getProcessByPid(pid);

        if(process == null) {
            throw new RuntimeException("Process not found with PID: " + pid);
        }
        return process;
    }
    @GetMapping("/search") 
    public List<ProcessInfoDTO> getProcessByName(@RequestParam String name) {
        return processMonitor.getProcessByName(name);
    }

    @GetMapping("/tree")
    public List<ProcessTreeDTO> getProcessTree() {
        return processMonitor.getProcessTree();
    }

    @GetMapping("/grouped")
    public List<ProcessGroupDTO> getGroupedProcesses() {
        return processMonitor.getGroupedProcesses();
    }

    @GetMapping("/sections") 
    public List<ProcessSectionDTO> getSectionProcesses() {
        return processMonitor.getSectionProcesses();
    }
}
