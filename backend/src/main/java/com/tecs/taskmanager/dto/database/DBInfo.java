package com.tecs.taskmanager.dto.database;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBInfo {
    private String name;
    private int pid;
    private List<Integer> ports;
}
