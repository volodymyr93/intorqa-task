package com.intorqa.task.rest;

import com.intorqa.task.persistence.ParsedDataService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;

public class RouterService {

    private final ParsedDataService parsedDataService;
    @Getter
    private final Router router;

    public RouterService(Vertx vertx, ParsedDataService parsedDataService) {
        this.parsedDataService = parsedDataService;
        this.router = Router.router(vertx);
        router.get("/reports").handler(this::getParsingData);
    }

    private void getParsingData(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(JsonObject.mapFrom(parsedDataService.getParsedData()).toString());
    }
}
