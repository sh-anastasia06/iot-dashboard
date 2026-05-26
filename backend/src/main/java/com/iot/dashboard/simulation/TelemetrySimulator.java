package com.iot.dashboard.simulation;

import com.iot.dashboard.db.TelemetryRepository;
import com.iot.dashboard.model.Node;
import com.iot.dashboard.model.TelemetryRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelemetrySimulator {
    private final TelemetryRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(32);
    private final Map<Integer, int[]> nodeStates = new HashMap<>(); // nodeId, [cpu, ram, ping]
    private final Random random = new Random();

    public TelemetrySimulator(TelemetryRepository repository) {
        this.repository = repository;
    }

    public void start(List<Node> nodes) {
        for (Node n : nodes) {
            nodeStates.put(n.getNodeId(), new int[] {
                    random.nextInt(40) + 20, // cpu
                    random.nextInt(40) + 20, // ram
                    random.nextInt(50) + 10  // ping
            });
        }

        // scheduling tasks per node
        for (Node n : nodes) {
            scheduler.scheduleAtFixedRate(() -> {
                TelemetryRecord record = generateRecord(n.getNodeId());
                repository.insertTelemetry(record);
            }, 0, 2, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        scheduler.shutdown();
        try {
            boolean finished = scheduler.awaitTermination(5, TimeUnit.SECONDS);
            if (!finished) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addNode(Node node) {
        nodeStates.put(node.getNodeId(), new int[]{
                random.nextInt(40) + 20,
                random.nextInt(40) + 20,
                random.nextInt(50) + 10
        });

        scheduler.scheduleAtFixedRate(() -> {
            TelemetryRecord record = generateRecord(node.getNodeId());
            repository.insertTelemetry(record);
        }, 0, 2, TimeUnit.SECONDS);
    }

    private TelemetryRecord generateRecord(int nodeId) {
        int[] state = nodeStates.get(nodeId);

        state[0] = Math.clamp(state[0] + random.nextInt(11) - 5, 0, 100);
        state[1] = Math.clamp(state[1] + random.nextInt(11) - 5, 0, 100);
        state[2] = Math.clamp(state[2] + random.nextInt(21) - 10, 1, 999);

        return new TelemetryRecord(
                0, // db will assign record_id
                nodeId,
                state[0],
                state[1],
                state[2],
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
