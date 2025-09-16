package com.tecs.taskmanager.TaskManager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PortFinder {
    public int getPortForPid(int pid) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                return getPortWindows(pid);
            } else {
                return getPortLinux(pid);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return -1;
    }

    public List<Integer> findPortsByPid(int pid) {
        List<Integer> ports = new ArrayList<>();
        try {
            Process p;
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

            ProcessBuilder pb;
            if (isWindows) {
                pb = new ProcessBuilder("netstat", "-ano", "-p", "tcp");
            } else {
                pb = new ProcessBuilder("lsof", "-i", "-P", "-n", "-p", String.valueOf(pid));
            }
            p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isWindows) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length > 0 && parts[parts.length - 1].equals(String.valueOf(pid))) {
                            if (line.contains("LISTENING")) {
                                if (parts.length >= 2 && parts[1].contains(":")) {
                                    try {
                                        String[] hostPort = parts[1].split(":");
                                        int port = Integer.parseInt(hostPort[hostPort.length - 1]);
                                        if (!ports.contains(port)) {
                                            ports.add(port);
                                        }
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                        }
                    } else {
                        if (line.contains("LISTEN")) {
                            String[] parts = line.trim().split("\\s+");
                            for (String part : parts) {
                                if (part.contains(":")) {
                                    try {
                                        int port = Integer.parseInt(part.substring(part.lastIndexOf(":") + 1));
                                        if (!ports.contains(port)) {
                                            ports.add(port);
                                        }
                                    } catch (NumberFormatException ignored) {
                                        // Ignore if it's not a valid port number (e.g. IPv6 address)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ports;
    }

    private static int getPortLinux(int pid) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("ss", "-tulpn");
        Process process = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader((process.getInputStream())))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("pid" + pid)) {
                    String[] parts = line.trim().split("\\s+");
                    String localAddress = parts[3];
                    String portStr = localAddress.substring(localAddress.lastIndexOf(":") + 1);
                    return Integer.parseInt(portStr);
                }
            }
        }
        return -1;
    }

    private static int getPortWindows(int pid) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("netstat", "-ano");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().endsWith(String.valueOf(pid))) {
                    String[] parts = line.trim().split("\\s+");
                    String localAddress = parts[1];
                    String portStr = localAddress.substring(localAddress.lastIndexOf(":") + 1);
                    return Integer.parseInt(portStr);
                }
            }
        }
        return -1;
    }
}
