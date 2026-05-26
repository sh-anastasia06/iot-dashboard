package com.iot.dashboard.api;

import com.iot.dashboard.db.TelemetryRepository;
import com.iot.dashboard.model.TelemetryRecord;
import com.iot.dashboard.model.TelemetrySummary;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TelemetryController {
    private final TelemetryRepository repository;

    public TelemetryController(TelemetryRepository repository) {
        this.repository = repository;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/telemetry/{nodeId}", this::getLatestRecords);
        app.get("/api/telemetry/{nodeId}/summary", this::getSummary);
    }

    private void getLatestRecords(Context ctx) {
        int nodeId = Integer.parseInt(ctx.pathParam("nodeId"));
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(40);

        List<TelemetryRecord> records = repository.getLatestRecords(nodeId, limit);
        ctx.json(records);
    }

    private void getSummary(Context ctx) {
        int nodeId = Integer.parseInt(ctx.pathParam("nodeId"));
        int window = ctx.queryParamAsClass("window", Integer.class).getOrDefault(30);

        Optional<TelemetrySummary> summary = repository.getSummary(nodeId, window);
        if (summary.isEmpty()) {
            ctx.status(400).json(Map.of("error", "No data found", "code", 404));
            return;
        }
        ctx.json(summary.get());
    }
}
