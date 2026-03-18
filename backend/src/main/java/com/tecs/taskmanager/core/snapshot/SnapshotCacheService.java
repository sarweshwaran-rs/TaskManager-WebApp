package com.tecs.taskmanager.core.snapshot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tecs.taskmanager.dto.snapshot.SystemSnapshotDTO;

import lombok.Getter;

@Service
@Getter
public class SnapshotCacheService {
    private final SystemSnapshotService snapshotService;
    
    private volatile SystemSnapshotDTO cachedSnapshot;
    private volatile long lastUpdated;

    public SnapshotCacheService(SystemSnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @Scheduled(fixedRate = 1000)
    public void refreshSnapshot() {
        try {
            this.cachedSnapshot = snapshotService.buildSnapshot();
            this.lastUpdated = System.currentTimeMillis();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public SystemSnapshotDTO getSnapshot() {
        if(cachedSnapshot == null) {
            refreshSnapshot();
        }
        return cachedSnapshot;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
