package com.iot.dashboard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Node {
    private final int nodeId;
    private final String nodeName;
    private final String ipAddress;
    private final String location;
    private final String registeredAt;

    public Node(int nodeId, String nodeName, String ipAddress, String location, String registeredAt){
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.ipAddress = ipAddress;
        this.location = location;
        this.registeredAt = registeredAt;
    }

    public Node(String nodeName, String ipAddress, String location) {
        this.nodeId = 0;  // DB will assign
        this.nodeName = nodeName;
        this.ipAddress = ipAddress;
        this.location = location;
        this.registeredAt = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public int getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }
}
