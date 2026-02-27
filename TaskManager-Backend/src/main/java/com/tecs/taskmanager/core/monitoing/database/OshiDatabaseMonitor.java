package com.tecs.taskmanager.core.monitoing.database;

import java.util.*;

import org.springframework.stereotype.Component;

import com.tecs.taskmanager.core.common.PortFinder;
import com.tecs.taskmanager.dto.database.DBInfo;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

@Component
public class OshiDatabaseMonitor implements DatabaseMonitor {

    // 🔥 Removed pg_ctl (not actual DB server)
    private static final List<String> DB_NAMES = Arrays.asList(
            "mysqld",
            "postgres",
            "mongod",
            "oracle",
            "sqlservr");

    private final OperatingSystem os;
    private final PortFinder portFinder;

    public OshiDatabaseMonitor(PortFinder portFinder) {
        SystemInfo systemInfo = new SystemInfo();
        this.os = systemInfo.getOperatingSystem();
        this.portFinder = portFinder;
    }

    @Override
    public List<DBInfo> detectDatabases() {

        Map<Integer, List<Integer>> portMap = portFinder.getListeningPortsByPid();

        Map<String, DBInfo> uniqueDB = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : portMap.entrySet()) {

            int pid = entry.getKey();
            List<Integer> ports = entry.getValue();

            OSProcess process = os.getProcess(pid);
            if (process == null)
                continue;

            String processName = process.getName().toLowerCase();

            for (String db : DB_NAMES) {

                if (processName.contains(db)
                        && !uniqueDB.containsKey(db)) {

                    uniqueDB.put(db,
                            new DBInfo(db, pid, ports));
                }
            }
        }

        return new ArrayList<>(uniqueDB.values());
    }
}
