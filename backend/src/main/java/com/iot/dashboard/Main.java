package com.iot.dashboard;

import com.iot.dashboard.db.DatabaseManager;
import com.iot.dashboard.db.TelemetryRepository;
import com.iot.dashboard.model.Node;
import com.iot.dashboard.server.ApiServer;
import com.iot.dashboard.simulation.TelemetrySimulator;

import java.util.List;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        System.out.println("Starting IoT Dashboard...");

        databaseManager.init("iot-dashboard.db");
        System.out.println("Database initialised");

        TelemetryRepository repository = new TelemetryRepository(databaseManager);
        TelemetrySimulator simulator = new TelemetrySimulator(repository);
        ApiServer server = new ApiServer(repository, simulator);

        if (!repository.hasNodes()) {
            List<Node> defaultNodes = List.of(
                    new Node("Sensor-01", "192.168.1.1", "London"),
                    new Node("Sensor-02", "192.168.1.2", "Tokyo"),
                    new Node("Sensor-03", "192.168.1.3", "Berlin"),
                    new Node("Sensor-04", "192.168.1.4", "New York"),
                    new Node("Sensor-05", "192.168.1.5", "Sydney"),
                    new Node("Sensor-06", "192.168.1.6", "San Paulo")
            );

            for (Node n : defaultNodes) {
                repository.insertNode(n);
            }
        }

        List<Node> nodes = repository.getAllNodes();
        System.out.println("Seeded " + nodes.size() + " default nodes");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            simulator.stop();
            server.stop();
            databaseManager.close();
            System.out.println("Shutdown complete");
        }));

        simulator.start(nodes);
        System.out.println("Simulator started for " + nodes.size() + " nodes");

        server.start(PORT);
    }
}
