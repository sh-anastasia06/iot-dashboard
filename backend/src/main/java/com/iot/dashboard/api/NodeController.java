package com.iot.dashboard.api;

import com.iot.dashboard.db.TelemetryRepository;
import com.iot.dashboard.model.Node;
import com.iot.dashboard.simulation.TelemetrySimulator;
import com.iot.dashboard.validation.InputValidator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class NodeController {
    private final TelemetryRepository repository;
    private final TelemetrySimulator simulator;

    public NodeController(TelemetryRepository repository, TelemetrySimulator simulator) {
        this.repository = repository;
        this.simulator = simulator;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/nodes", this::getNodes);
        app.post("/api/nodes", this::createNode);
    }

    private void getNodes(Context ctx) {
        List<Node> nodes = repository.getAllNodes();
        ctx.json(nodes);
    }

    private void createNode(Context ctx) {
        Node node = ctx.bodyAsClass(Node.class);

        if (!InputValidator.isValidIp(node.getIpAddress())) {
            ctx.status(400).json(Map.of("error", "Invalid IP address", "code", 400));
            return;
        }

        if (!InputValidator.isValidNodeName(node.getNodeName())) {
            ctx.status(400).json(Map.of("error", "Invalid node name", "code", 400));
            return;
        }

        if (!InputValidator.isValidLocation(node.getLocation())) {
            ctx.status(400).json(Map.of("error", "Invalid location", "code", 400));
            return;
        }

        repository.insertNode(node);
        simulator.addNode(node);
        ctx.status(201).json(node); // code 201 - created
    }
}
