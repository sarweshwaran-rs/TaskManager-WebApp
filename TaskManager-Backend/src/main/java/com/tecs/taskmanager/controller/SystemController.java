package com.tecs.taskmanager.controller;

import com.tecs.taskmanager.core.snapshot.SystemSnapshotService;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
@RestController
@RequestMapping("/api")
public class SystemController {
    private final SystemSnapshotService service;

    public SystemController(SystemSnapshotService service) {
        super();
        this.service = service;
    }

    @GetMapping("/snapshot")
    public SystemSnapshotDTO getSnapshot() {
        return service.getSnapshot();
    }
}
