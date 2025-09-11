package com.tecs.taskmanager.TaskManager.controller;

import com.tecs.taskmanager.TaskManager.service.SystemMonitorService;

//import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MetricWebSocketController {
    private final SystemMonitorService service;
    private final SimpMessagingTemplate messagingTemplate;


    public MetricWebSocketController(SystemMonitorService service, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 2000)
    public void getMetrics() {
        Map<String, Object> data = new HashMap<>();
        data.put("cpu", service.getCpuInfo());
        data.put("memory", service.getMemoryInfo());
        data.put("os", service.getOsInfo());
        data.put("processes", service.getProcesses(10));
        data.put("gpu", service.getGpuInfo());
        data.put("disk", service.getDiskInfo());
        data.put("network", service.getNetworkInfo());
        
        messagingTemplate.convertAndSend("/api/metrics", data);
    }
}
