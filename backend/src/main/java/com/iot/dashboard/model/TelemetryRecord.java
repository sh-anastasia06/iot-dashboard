package com.iot.dashboard.model;

public class TelemetryRecord {
    private final int recordId;
    private final int nodeId;
    private final int cpuUsage;
    private final int ramUsage;
    private final int netPingsMs;
    private final String recordedAt;

    public TelemetryRecord(int recordId, int nodeId, int cpuUsage, int ramUsage, int netPingsMs, String recordedAt) {
        this.recordId = recordId;
        this.nodeId = nodeId;
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.netPingsMs = netPingsMs;
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

    public int getNetPingsMs() {
        return netPingsMs;
    }

    public String getRecordedAt() {
        return recordedAt;
    }
}
