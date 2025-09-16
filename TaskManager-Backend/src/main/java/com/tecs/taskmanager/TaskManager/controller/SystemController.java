package com.tecs.taskmanager.TaskManager.controller;

import com.tecs.taskmanager.TaskManager.service.SystemMonitorService;
import com.tecs.taskmanager.TaskManager.model.DBInfo;
import com.tecs.taskmanager.TaskManager.service.DBService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class SystemController {
    private final SystemMonitorService service;
    private final DBService dbService;

    public SystemController(SystemMonitorService service, DBService dbService) {
        this.service = service;
        this.dbService = dbService;
    }

    @GetMapping("/cpu")
    public Map<String, Object> getCpuInfo() {
        return service.getCpuInfo();
    }

    @GetMapping("/memory")
    public Map<String, Object> getMemoryInfo() {
        return service.getMemoryInfo();
    }
    
    @GetMapping("/os")
    public Map<String, Object> getOsInfo() {
        return service.getOsInfo();
    }    

    @GetMapping("/processes")
    public List<Map<String, Object>> getProcesses(@RequestParam(defaultValue = "10") int limit) {
        return service.getProcesses(limit);
    }

    @GetMapping("/gpu")
    public List<Map<String, Object>> getGpuInfo() {
        return service.getGpuInfo();
    }

    @GetMapping("/disk")
    public List<Map<String, Object>> getDiskInfo() {
        return service.getDiskInfo();
    }

    @GetMapping("/network")
    public List<Map<String, Object>> getNetworkInfo() {
        return service.getNetworkInfo();
    }

    @GetMapping("/computer")
    public Map<String, Object> getComputerInfo() {
        return service.getComputerInfo();
    }

    @GetMapping("/db")
    public List<DBInfo> getDBInfo() {
        return dbService.detectDBProcesses();
    }
}
