package com.tecs.taskmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecs.taskmanager.core.snapshot.SnapshotCacheService;
import com.tecs.taskmanager.dto.common.ApiResponse;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

@RestController
@RequestMapping("/api")
public class SystemController {

    private final SnapshotCacheService cacheService;

    public SystemController(SnapshotCacheService cacheService) {
        super();
        this.cacheService = cacheService;
    }

    private SystemSnapshotDTO getSnapshotSafe() {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();
        if (snapshot == null) {
            throw new RuntimeException("System snapshot not available");
        }
        return snapshot;
    }

    @GetMapping("/snapshot")
    public ResponseEntity<ApiResponse<SystemSnapshotDTO>> getSnapshot() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe())
        );
    }

    @GetMapping("/cpu")
    public ResponseEntity<ApiResponse<?>> getCpu() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getCpu())
        );
    }

    @GetMapping("/memory")
    public ResponseEntity<ApiResponse<?>> getMemory() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getMemory())
        );
    }

    @GetMapping("/disks")
    public ResponseEntity<ApiResponse<?>> getDisks() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getDisks())
        );
    }

    @GetMapping("/gpus")
    public ResponseEntity<ApiResponse<?>> getGPU() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getGpus())
        );
    }

    @GetMapping("/os")
    public ResponseEntity<ApiResponse<?>> getOS() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getOs())
        );
    }

    @GetMapping("/computer-info")
    public ResponseEntity<ApiResponse<?>> getCI() {
        return ResponseEntity.ok(
                ApiResponse.success(getSnapshotSafe().getComputerInfo())
        );
    }
}
