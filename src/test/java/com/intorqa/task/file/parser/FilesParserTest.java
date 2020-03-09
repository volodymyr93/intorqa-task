package com.intorqa.task.file.parser;

import com.google.common.collect.ImmutableList;
import com.intorqa.task.file.ParsedRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class FilesParserTest {

    @TempDir
    public Path tempDir;

    @BeforeEach
    public void init(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(
                new FilesParser(),
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    public void shouldParseFile(Vertx vertx, VertxTestContext testContext) throws IOException {
        Path newFile = tempDir.resolve(UUID.randomUUID().toString());
        Files.write(newFile, ImmutableList.of("first line", "second line\n"), StandardCharsets.UTF_8);
        List<String> words = ImmutableList.of("first", "line", "second", "line");

        Checkpoint checkpoint = testContext.checkpoint(4);

        vertx.eventBus().publish("files.changed", new JsonArray(ImmutableList.of(newFile.toString())));

        vertx.eventBus().<JsonObject>consumer("words.parsed", message -> {
            testContext.verify(() -> {
                ParsedRecord record = message.body().mapTo(ParsedRecord.class);
                assertThat(words).contains(record.getWord());
                assertThat(newFile.toString()).contains(record.getFileName());
                checkpoint.flag();
                testContext.completeNow();
            });
        });
    }

    @AfterEach
    public void destroy(Vertx vertx) {
        vertx.close();
    }
}
