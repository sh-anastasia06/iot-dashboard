package com.iot.dashboard.server;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iot.dashboard.api.NodeController;
import com.iot.dashboard.api.TelemetryController;
import com.iot.dashboard.db.TelemetryRepository;
import com.iot.dashboard.simulation.TelemetrySimulator;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class ApiServer {
    private final TelemetryRepository repository;
    private final TelemetrySimulator simulator;
    private Javalin app;

    public ApiServer(TelemetryRepository repository, TelemetrySimulator simulator) {
        this.repository = repository;
        this.simulator = simulator;
    }

    public void start(int port) {
        app = Javalin.create(config -> {
            // CORS
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.anyHost(); // for development only
                });
            });

            // handling Java 8 date/time types
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
            }));

            config.requestLogger.http((ctx, ms) ->
                    System.out.println(ctx.method() + " " + ctx.path() + " → " + ctx.status() + " (" + ms + "ms)")
            );
        });

        registerRoutes();

        app.events(event -> {
            event.serverStarted(() ->
                    System.out.println("API ready at http://localhost:" + port)
            );
        });

        app.start(port);
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }

    private void registerRoutes() {
        new NodeController(repository, simulator).registerRoutes(app);
        new TelemetryController(repository).registerRoutes(app);
    }
}
