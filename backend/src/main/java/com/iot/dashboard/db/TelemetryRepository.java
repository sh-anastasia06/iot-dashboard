package com.iot.dashboard.db;

import com.iot.dashboard.model.Node;
import com.iot.dashboard.model.TelemetryRecord;
import com.iot.dashboard.model.TelemetrySummary;

import javax.swing.text.html.Option;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelemetryRepository {
    private final DatabaseManager databaseManager;

    public TelemetryRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void insertTelemetry(TelemetryRecord record) {
        String sql = "INSERT INTO telemetry (node_id, cpu_usage, ram_usage, net_ping_ms, recorded_at)" +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, record.getNodeId());
            stmt.setInt(2, record.getCpuUsage());
            stmt.setInt(3, record.getRamUsage());
            stmt.setInt(4, record.getNetPingMs());
            stmt.setString(5, record.getRecordedAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert telemetry", e);
        }
    }

    public void insertNode(Node node) {
        String sql = "INSERT INTO nodes (node_name, ip_address, location, registered_at)" +
                " VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, node.getNodeName());
            stmt.setString(2, node.getIpAddress());
            stmt.setString(3, node.getLocation());
            stmt.setString(4, node.getRegisteredAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw  new RuntimeException("Failed to insert node", e);
        }
    }

    public List<TelemetryRecord> getLatestRecords(int nodeId, int limit) {
        String sql = "SELECT * FROM telemetry WHERE node_id = ? ORDER BY recorded_at DESC LIMIT ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nodeId);
            stmt.setInt(2, limit);

            List<TelemetryRecord> results = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new TelemetryRecord(
                            rs.getInt("record_id"),
                        rs.getInt("node_id"),
                        rs.getInt("cpu_usage"),
                        rs.getInt("ram_usage"),
                        rs.getInt("net_ping_ms"),
                        rs.getString("recorded_at")
                    ));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get latest records", e);
        }
    }

    public List<Node> getAllNodes() {
        String sql = "SELECT * FROM nodes ORDER BY registered_at DESC";
        try (Connection connection = databaseManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Node> nodes = new ArrayList<>();
            while (rs.next()) {
                nodes.add(new Node(
                        rs.getInt("node_id"),
                        rs.getString("node_name"),
                        rs.getString("ip_address"),
                        rs.getString("location"),
                        rs.getString("registered_at")
                ));
            }
            return nodes;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all nodes", e);
        }

    }

    public Optional<TelemetrySummary> getSummary(int nodeId, int windowMinutes) {
        String sql = """
            SELECT
                AVG(cpu_usage)   as avg_cpu,
                MAX(cpu_usage)   as max_cpu,
                AVG(ram_usage)   as avg_ram,
                MAX(ram_usage)   as max_ram,
                AVG(net_ping_ms) as avg_ping,
                MAX(net_ping_ms) as max_ping
            FROM telemetry
            WHERE node_id = ?
            AND recorded_at >= datetime('now', ? || ' minutes')
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nodeId);
            stmt.setString(2, "-" + windowMinutes);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            new TelemetrySummary(
                                    rs.getInt("avg_cpu"),
                                    rs.getInt("max_cpu"),
                                    rs.getInt("avg_ram"),
                                    rs.getInt("max_ram"),
                                    rs.getInt("avg_ping"),
                                    rs.getInt("max_ping"),
                                    windowMinutes
                            )
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get summary", e);
        }

        return Optional.empty();
    }

    public boolean hasNodes() {
        String sql = "SELECT COUNT(*) FROM nodes";
        try (Connection connection = databaseManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to check nodes", e);
        }
        return false;
    }
}
