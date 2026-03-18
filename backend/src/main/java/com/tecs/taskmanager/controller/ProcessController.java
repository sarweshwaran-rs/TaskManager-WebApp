package com.tecs.taskmanager.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tecs.taskmanager.core.monitoing.process.ProcessMonitor;
import com.tecs.taskmanager.core.snapshot.SnapshotCacheService;
import com.tecs.taskmanager.dto.process.ProcessGroupDTO;
import com.tecs.taskmanager.dto.process.ProcessInfoDTO;
import com.tecs.taskmanager.dto.process.ProcessKillPreviewDTO;
import com.tecs.taskmanager.dto.process.ProcessKillResponseDTO;
import com.tecs.taskmanager.dto.process.ProcessSectionDTO;
import com.tecs.taskmanager.dto.process.ProcessTreeDTO;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

@RestController
@RequestMapping("/api/processes")
public class ProcessController {

    private final SnapshotCacheService cacheService;
    private final ProcessMonitor processMonitor;

    public ProcessController(SnapshotCacheService cacheService, ProcessMonitor processMonitor) {
        this.cacheService = cacheService;
        this.processMonitor = processMonitor;
    }

    @GetMapping
    public ResponseEntity<List<ProcessInfoDTO>> getAllProcesses() {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();

        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok(snapshot.getProcesses());
    }

    @GetMapping("/top")
    public ResponseEntity<List<ProcessInfoDTO>> getTopProcesses(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();

        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        List<ProcessInfoDTO> top = snapshot.getProcesses()
                .stream()
                .sorted(Comparator.comparingDouble(ProcessInfoDTO::getCpuLoad).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        return ResponseEntity.ok(top);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProcessInfoDTO>> searchByName(@RequestParam(name = "name") String name) {

        SystemSnapshotDTO snapshot = cacheService.getSnapshot();

        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        List<ProcessInfoDTO> result = snapshot.getProcesses()
                .stream()
                .filter(p -> p.getName() != null &&
                        p.getName().equalsIgnoreCase(name))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<ProcessTreeDTO>> getProcessTree() {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();
        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok(snapshot.getProcessTree());
    }

    @GetMapping("/{pid}")
    public ResponseEntity<ProcessTreeDTO> getProcessByPid(@PathVariable("pid") int pid) {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();
        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        ProcessTreeDTO result = findProcess(snapshot.getProcessTree(), pid);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/grouped")
    public ResponseEntity<List<ProcessGroupDTO>> getGroupedProcesses() {

        SystemSnapshotDTO snapshot = cacheService.getSnapshot();

        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        return ResponseEntity.ok(snapshot.getGroupedProcesses());
    }

    @GetMapping("/sections")
    public ResponseEntity<List<ProcessSectionDTO>> getSectionProcesses() {

        SystemSnapshotDTO snapshot = cacheService.getSnapshot();

        if (snapshot == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok(snapshot.getProcessSections());
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<ProcessKillResponseDTO> killProcess(@PathVariable int pid,
            @RequestParam(defaultValue = "false") boolean force) {
        ProcessKillResponseDTO response = processMonitor.killProcess(pid, force);

        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
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
    public ResponseEntity<ProcessKillResponseDTO> killProcessTree(@PathVariable int pid,
            @RequestParam(defaultValue = "false") boolean force) {
        ProcessKillResponseDTO response = processMonitor.killProcessTree(pid, force);

        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    private ProcessTreeDTO findProcess(List<ProcessTreeDTO> roots, int pid) {
        for (ProcessTreeDTO root : roots) {
            if (root.getPid() == pid) {
                return root;
            }

            ProcessTreeDTO found = findProcess(root.getChildren(), pid);

            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
