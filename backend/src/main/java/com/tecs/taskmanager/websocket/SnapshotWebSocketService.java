package com.tecs.taskmanager.websocket;

import java.time.LocalTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tecs.taskmanager.core.snapshot.SnapshotCacheService;

@Service
public class SnapshotWebSocketService {
    private final SnapshotCacheService cacheService;
    private final SimpMessagingTemplate messagingTemplate;

    public SnapshotWebSocketService(SnapshotCacheService cacheService, SimpMessagingTemplate messagingTemplate) {
        this.cacheService = cacheService;
        this.messagingTemplate = messagingTemplate;
    }

    private String lastHash = "";

    @Scheduled(fixedRate = 1000)
    public void pushSnapshot() {
        var snapshot = cacheService.getSnapshot();

        String currentHash = String.valueOf(snapshot.hashCode());

        if(!currentHash.equals(lastHash)) {
            messagingTemplate.convertAndSend("/topic/snapshot", snapshot);
            lastHash = currentHash;
            System.out.println("WebSocket Push at: " + LocalTime.now());
        }
    }
}
