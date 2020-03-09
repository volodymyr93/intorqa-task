package com.intorqa.task.rest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpServer extends AbstractVerticle {

    private final RouterService router;

    @Override
    public void start(Promise<Void> promise) {
        vertx.createHttpServer()
                .requestHandler(router.getRouter())
                .listen(config().getInteger("http.port"), result -> {
                    if (result.succeeded()) {
                        promise.complete();
                    } else {
                        promise.fail(result.cause());
                    }
                });
    }
}
