package com.tecs.taskmanager.TaskManager.service;

import com.tecs.taskmanager.TaskManager.model.DBInfo;
import com.tecs.taskmanager.TaskManager.util.PortFinder;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DBService {

        private static final List<String> DB_Names = Arrays.asList(
        "mysqld", "pg_ctl", "mongod", "oracle", "sqlservr"
    );


    @SuppressWarnings("unused")
    private DBInfo mapToDBInfo(OSProcess process) {
        List<Integer> ports;
        try {
            PortFinder portFinder = new PortFinder();
            ports = portFinder.findPortsByPid(process.getProcessID());
        } catch (Exception e) {
            e.printStackTrace();
            ports = new ArrayList<>();
        }
        return new DBInfo(process.getName(), process.getProcessID(), ports);
    }

    public List<DBInfo> detectDBProcesses() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        List<OSProcess> processes = os.getProcesses();

        Map<String, DBInfo> uniqueDb = new HashMap<>();
        PortFinder portFinder = new PortFinder();
        
        for (OSProcess p : processes) {
            String name = p.getName().toLowerCase();
            for(String db : DB_Names) {
                if(name.contains(db) && !uniqueDb.containsKey(db)) {
                    List<Integer> ports = portFinder.findPortsByPid(p.getProcessID());
                    uniqueDb.put(db, new DBInfo(db, p.getProcessID(), ports));
                }
            }
        }
        return new ArrayList<>(uniqueDb.values());
    }
}
