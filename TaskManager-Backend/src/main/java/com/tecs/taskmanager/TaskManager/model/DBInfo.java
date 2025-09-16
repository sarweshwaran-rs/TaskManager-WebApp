package com.tecs.taskmanager.TaskManager.model;

import java.util.List;

public class DBInfo {
    private String name;
    private int pid;
    private List<Integer> ports;

    public DBInfo(String name, int pid, List<Integer> ports) {
        this.name = name;
        this.pid = pid;
        this.ports = ports;
    }

    public String getName() {
        return name;
    }

    public int getPid() {
        return pid;
    }

    public List<Integer> getPorts() {
        return ports;
    }
}
