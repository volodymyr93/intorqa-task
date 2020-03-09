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

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@ExtendWith(VertxExtension.class)
public class WordsUniqueCountProcessorTest {

    private ParsedDataStorage storage = new ParsedDataStorage();

    @BeforeEach
    public void init(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(
                new WordsUniqueCountProcessor(storage),
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    public void shouldCountProcessedFiles(Vertx vertx, VertxTestContext testContext) {
        vertx.eventBus().publish("words.parsed", JsonObject.mapFrom(new ParsedRecord("file", "word1")));
        vertx.eventBus().publish("words.parsed", JsonObject.mapFrom(new ParsedRecord("file", "word1")));
        vertx.eventBus().publish("words.parsed", JsonObject.mapFrom(new ParsedRecord("file", "word2")));

        testContext.verify(() -> {
            await().atMost(Duration.ofSeconds(10))
                    .until(() -> storage.getProcessedWords().containsAll(ImmutableList.of("word1", "word2")));
            testContext.completeNow();
        });
    }

    @AfterEach
    public void destroy(Vertx vertx) {
        vertx.close();
    }
}
