package com.tecs.taskmanager.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecs.taskmanager.core.monitoing.process.ProcessMonitor;
import com.tecs.taskmanager.core.snapshot.SnapshotCacheService;
import com.tecs.taskmanager.dto.common.ApiResponse;
import com.tecs.taskmanager.dto.process.*;
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

    private SystemSnapshotDTO getSnapshotSafe() {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();
        if (snapshot == null) {
            throw new RuntimeException("System snapshot not available");
        }
        return snapshot;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProcessInfoDTO>>> getAllProcesses() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getProcesses())
        );
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<ProcessInfoDTO>>> getTopProcesses(@RequestParam(defaultValue = "10") int limit) {

        List<ProcessInfoDTO> top = getSnapshotSafe().getProcesses()
                .stream()
                .sorted(Comparator.comparingDouble(ProcessInfoDTO::getCpuLoad).reversed())
                .limit(limit)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(top));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProcessInfoDTO>>> searchByName(@RequestParam String name) {

        List<ProcessInfoDTO> result = getSnapshotSafe().getProcesses()
                .stream()
                .filter(p -> p.getName() != null &&
                        p.getName().equalsIgnoreCase(name))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<ProcessTreeDTO>>> getProcessTree() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getProcessTree())
        );
    }

    @GetMapping("/{pid}")
    public ResponseEntity<ApiResponse<ProcessTreeDTO>> getProcessByPid(@PathVariable int pid) {

        ProcessTreeDTO result = findProcess(getSnapshotSafe().getProcessTree(), pid);

        if (result == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Process not found"));
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<List<ProcessGroupDTO>>> getGroupedProcesses() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getGroupedProcesses())
        );
    }

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<ProcessSectionDTO>>> getSectionProcesses() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getProcessSections())
        );
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<ApiResponse<ProcessKillResponseDTO>> killProcess(@PathVariable int pid, @RequestParam(defaultValue = "false") boolean force) {
        ProcessKillResponseDTO response = processMonitor.killProcess(pid, force);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(response.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{pid}/preview-kill")
    public ResponseEntity<ApiResponse<ProcessKillPreviewDTO>> previewKillTree( @PathVariable int pid) {

        ProcessKillPreviewDTO preview = processMonitor.previewKillTree(pid);

        if (preview == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Process not found"));
        }

        return ResponseEntity.ok(ApiResponse.success(preview));
    }

    @DeleteMapping("/{pid}/kill-tree")
    public ResponseEntity<ApiResponse<ProcessKillResponseDTO>> killProcessTree( @PathVariable int pid, @RequestParam(defaultValue = "false") boolean force) {
        ProcessKillResponseDTO response = processMonitor.killProcessTree(pid, force);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(response.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private ProcessTreeDTO findProcess(List<ProcessTreeDTO> roots, int pid) {
        for (ProcessTreeDTO root : roots) {
            if (root.getPid() == pid) {
                return root;
            }

            ProcessTreeDTO found = findProcess(root.getChildren(), pid);

            if (found != null) return found;
        }
        return null;
    }
}
