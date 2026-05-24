package com.iot.dashboard.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private HikariDataSource dataSource;

    public void init(String dbPath){
        HikariConfig config = new HikariConfig();

        // connection settings
        config.setJdbcUrl("jdbc:sqlite:" + dbPath);

        // pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2); // keep 2 ready even when idle

        dataSource = new HikariDataSource(config);
        enableWal();
        createTables();
    }

    // WAL mode
    private void enableWal() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable WAL mode", e);
        }
    }

    private void createTables() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            // nodes
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS nodes (
                    node_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    node_name     TEXT    NOT NULL,
                    ip_address    TEXT    NOT NULL,
                    location      TEXT    NOT NULL,
                    registered_at TEXT    NOT NULL
                )
                """);

             // telemetry
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS telemetry (
                    record_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                    node_id     INTEGER NOT NULL,
                    cpu_usage   INTEGER NOT NULL,
                    ram_usage   INTEGER NOT NULL,
                    net_ping_ms INTEGER NOT NULL,
                    recorded_at TEXT    NOT NULL,
                    FOREIGN KEY (node_id) REFERENCES nodes(node_id)
                )
                """);

            // alerts
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS alerts (
                    alert_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    node_id       INTEGER NOT NULL,
                    metric        TEXT    NOT NULL,
                    threshold_val INTEGER NOT NULL,
                    triggered_at  TEXT    NOT NULL,
                    FOREIGN KEY (node_id) REFERENCES nodes(node_id)
                )
                """);

            // index
            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_telemetry_node_time
                ON telemetry(node_id, recorded_at)
                """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return  dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) dataSource.close();
    }
}
