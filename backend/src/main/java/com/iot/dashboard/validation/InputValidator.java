package com.iot.dashboard.validation;

public class InputValidator {
    private static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isBlank()) return false;
        return ip.matches(IP_REGEX);
    }

    public static boolean isValidNodeName(String name) {
        return name != null && !name.isBlank() && name.length() <= 50;
    }

    public static boolean isValidLocation(String location) {
        return location != null && !location.isBlank() && location.length() <= 100;
    }
}
