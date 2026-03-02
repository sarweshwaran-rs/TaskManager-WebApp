package com.tecs.taskmanager.controller;

import com.tecs.taskmanager.core.snapshot.SnapshotCacheService;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
@RestController
@RequestMapping("/api")
public class SystemController {
    private final SnapshotCacheService cacheService;

    public SystemController(SnapshotCacheService cacheService) {
        super();
        this.cacheService = cacheService;
    }

    @GetMapping("/snapshot")
    public ResponseEntity<SystemSnapshotDTO> getSnapshot() {
        SystemSnapshotDTO snapshot = cacheService.getSnapshot();
        return ResponseEntity.ok(snapshot);
    }

    @GetMapping("/cpu")
    public ResponseEntity<?> getCpu() {
        return ResponseEntity.ok(cacheService.getSnapshot().getCpu());
    }

    @GetMapping("/memory")
    public ResponseEntity<?> getMemory() {
        return ResponseEntity.ok(cacheService.getSnapshot().getMemory());
    }

    @GetMapping("/disks")
    public ResponseEntity<?> getDisks() {
        return ResponseEntity.ok(cacheService.getSnapshot().getDisks());
    }

    @GetMapping("/sprocesses")
    public ResponseEntity<?> getProcesses() {
        return ResponseEntity.ok(cacheService.getSnapshot().getProcesses());
    }
}
