package com.tecs.taskmanager.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.tecs.taskmanager.core.snapshot.SystemSnapshotService;
import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

@Controller
public class MetricWebSocketController {

    private final SystemSnapshotService snapshotService;
    private final SimpMessagingTemplate messagingTemplate;

    public MetricWebSocketController(SystemSnapshotService snapshotService, SimpMessagingTemplate messagingTemplate) {
        this.snapshotService = snapshotService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void publishMetrics() {

        SystemSnapshotDTO snapshot = snapshotService.getSnapshot(10);

        if (snapshot != null) {
            messagingTemplate.convertAndSend("/api/metrics", snapshot);
        }
    }
}