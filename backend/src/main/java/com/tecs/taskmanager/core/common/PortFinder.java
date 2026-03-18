package com.tecs.taskmanager.core.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PortFinder {

    private static final Logger logger = LoggerFactory.getLogger(PortFinder.class);

    /**
     * Scans system once and returns:
     * PID -> List of UNIQUE LISTENING ports
     */
    public Map<Integer, List<Integer>> getListeningPortsByPid() {

        // 🔥 Use Set to remove duplicates automatically
        Map<Integer, Set<Integer>> tempMap = new HashMap<>();

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase()
                .contains("win");

        try {

            ProcessBuilder pb;

            if (isWindows) {
                pb = new ProcessBuilder("netstat", "-ano");
            } else {
                pb = new ProcessBuilder("ss", "-tulpn");
            }

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    line = line.trim();

                    if (isWindows) {
                        parseWindowsLine(line, tempMap);
                    } else {
                        parseLinuxLine(line, tempMap);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error scanning listening ports", e);
        }

        // 🔥 Convert Set → List before returning
        Map<Integer, List<Integer>> result = new HashMap<>();

        for (Map.Entry<Integer, Set<Integer>> entry : tempMap.entrySet()) {
            result.put(entry.getKey(),
                    new ArrayList<>(entry.getValue()));
        }

        return result;
    }

    // ================= WINDOWS =================

    private void parseWindowsLine(String line,
            Map<Integer, Set<Integer>> pidPortMap) {

        if (!line.startsWith("TCP"))
            return;
        if (!line.contains("LISTENING"))
            return;

        String[] parts = line.split("\\s+");
        if (parts.length < 5)
            return;

        try {

            String localAddress = parts[1];
            int pid = Integer.parseInt(parts[4]);

            int port = extractPort(localAddress);
            if (port <= 0)
                return;

            pidPortMap
                    .computeIfAbsent(pid, k -> new HashSet<>())
                    .add(port);

        } catch (Exception ignored) {
        }
    }

    // ================= LINUX =================

    private void parseLinuxLine(String line,
            Map<Integer, Set<Integer>> pidPortMap) {

        if (!line.contains("LISTEN"))
            return;
        if (!line.contains("pid="))
            return;

        try {

            int pidStart = line.indexOf("pid=") + 4;
            int pidEnd = line.indexOf(",", pidStart);

            int pid = Integer.parseInt(
                    line.substring(pidStart, pidEnd));

            String[] parts = line.split("\\s+");

            for (String part : parts) {
                if (part.contains(":")) {

                    int port = extractPort(part);

                    if (port > 0) {
                        pidPortMap
                                .computeIfAbsent(pid,
                                        k -> new HashSet<>())
                                .add(port);
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    // ================= COMMON =================

    private int extractPort(String address) {

        try {

            // IPv6 format
            if (address.contains("]:")) {
                return Integer.parseInt(
                        address.substring(
                                address.lastIndexOf("]:") + 2));
            }

            return Integer.parseInt(
                    address.substring(
                            address.lastIndexOf(":") + 1));

        } catch (Exception e) {
            return -1;
        }
    }
}