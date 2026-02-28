package com.tecs.taskmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecs.taskmanager.core.monitoing.process.ProcessMonitor;
import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessKillPreviewDTO;
import com.tecs.taskmanager.dto.process.ProcessKillResponseDTO;
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

    @GetMapping("/{pid}")
    public ResponseEntity<ProcessTreeDTO> getProcessByPid(@PathVariable int pid) {

        ProcessTreeDTO process = processMonitor.getProcessByPid(pid);

        if (process == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(process);
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<ProcessKillResponseDTO> killProcess(@PathVariable int pid, @RequestParam(defaultValue = "false") boolean force) {

        ProcessKillResponseDTO response = processMonitor.killProcess(pid, force);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{pid}/preview-kill")
    public ResponseEntity<ProcessKillPreviewDTO> previewKillTree(@PathVariable int pid) {

        ProcessKillPreviewDTO preview = processMonitor.previewKillTree(pid);

        if (preview == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(preview);
    }

    @DeleteMapping("/{pid}/kill-tree")
    public ResponseEntity<ProcessKillResponseDTO> killProcessTree(@PathVariable int pid, @RequestParam(defaultValue = "false") boolean force) {

        ProcessKillResponseDTO response = processMonitor.killProcessTree(pid, force);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
