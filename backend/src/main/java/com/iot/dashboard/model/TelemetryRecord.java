package com.iot.dashboard.model;

public class TelemetryRecord {
    private final int recordId;
    private final int nodeId;
    private final int cpuUsage;
    private final int ramUsage;
    private final int netPingMs;
    private final String recordedAt;

    public TelemetryRecord(int recordId, int nodeId, int cpuUsage, int ramUsage, int netPingMs, String recordedAt) {
        this.recordId = recordId;
        this.nodeId = nodeId;
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.netPingMs = netPingMs;
        this.recordedAt = recordedAt;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getCpuUsage() {
        return cpuUsage;
    }

    public int getRamUsage() {
        return ramUsage;
    }

    public int getNetPingMs() {
        return netPingMs;
    }

    public String getRecordedAt() {
        return recordedAt;
    }
}
