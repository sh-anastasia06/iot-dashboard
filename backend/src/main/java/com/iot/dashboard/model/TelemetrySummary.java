package com.iot.dashboard.model;

public class TelemetrySummary {
    private final int avgCpu;
    private final int maxCpu;
    private final int avgRam;
    private final int maxRam;
    private final int avgPing;
    private final int maxPing;
    private final int windowMinutes;

    public TelemetrySummary(int avgCpu, int maxCpu, int avgRam, int maxRam, int avgPing, int maxPing, int windowMinutes) {
        this.avgCpu = avgCpu;
        this.maxCpu = maxCpu;
        this.avgRam = avgRam;
        this.maxRam = maxRam;
        this.avgPing = avgPing;
        this.maxPing = maxPing;
        this.windowMinutes = windowMinutes;
    }

    public int getAvgCpu() {
        return avgCpu;
    }

    public int getMaxCpu() {
        return maxCpu;
    }

    public int getAvgRam() {
        return avgRam;
    }

    public int getMaxRam() {
        return maxRam;
    }

    public int getAvgPing() {
        return avgPing;
    }

    public int getMaxPing() {
        return maxPing;
    }

    public int getWindowMinutes() {
        return windowMinutes;
    }
}
