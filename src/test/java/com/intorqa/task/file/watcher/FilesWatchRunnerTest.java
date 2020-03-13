package com.intorqa.task.file.watcher;

import com.google.common.collect.ImmutableMap;
import com.intorqa.task.persistence.ProcessedFilesStorage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class FilesWatchRunnerTest {

    @Mock
    private FilesWatchService watchService;

    @BeforeEach
    public void init(Vertx vertx, VertxTestContext testContext) {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("files.watch.iteration", 1000));
        vertx.deployVerticle(
                new FilesWatchRunner(watchService, new ProcessedFilesStorage()),
                options,
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    public void shouldSendFilesForProcessing(Vertx vertx, VertxTestContext testContext) {
        when(watchService.poll()).thenReturn(ImmutableMap.of("file1", 10L, "file2", 10L));

        vertx.eventBus().<JsonArray>consumer("files.changed", message -> {
            testContext.verify(() -> {
                assertThat(
                        message.body().stream()
                                .collect(Collectors.toList())
                ).containsExactlyInAnyOrder("file1", "file2");
                testContext.completeNow();
            });
        });
    }

    @AfterEach
    public void destroy(Vertx vertx) {
        vertx.close();
    }
}
