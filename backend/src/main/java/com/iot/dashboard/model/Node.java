package com.iot.dashboard.model;

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
