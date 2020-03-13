package com.intorqa.task.processor;

import com.google.common.collect.ImmutableList;
import com.intorqa.task.file.ParsedRecord;
import com.intorqa.task.persistence.ParsedDataStorage;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith(VertxExtension.class)
public class FilesCountProcessorTest {

    private ParsedDataStorage storage = new ParsedDataStorage();

    @BeforeEach
    public void init(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(
                new FilesCountProcessor(storage),
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    public void shouldCountProcessedFiles(Vertx vertx, VertxTestContext testContext) {
        vertx.eventBus().publish("words.parsed", JsonObject.mapFrom(new ParsedRecord("file1", "word")));
        vertx.eventBus().publish("words.parsed", JsonObject.mapFrom(new ParsedRecord("file2", "word")));

        testContext.verify(() -> {
            await().atMost(Duration.ofSeconds(10))
                    .until(() -> storage.getProcessedFiles().containsAll(ImmutableList.of("file1", "file2"))
                    );
            testContext.completeNow();
        });
    }

    @AfterEach
    public void destroy(Vertx vertx) {
        vertx.close();
    }
}
