package com.tecs.taskmanager.core.monitoing.database;

import java.util.List;

import com.tecs.taskmanager.dto.database.DBInfo;

public interface DatabaseMonitor {
    List<DBInfo> detectDatabases();
}
