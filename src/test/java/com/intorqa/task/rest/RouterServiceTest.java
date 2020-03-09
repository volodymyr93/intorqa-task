package com.intorqa.task.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.intorqa.task.persistence.ParsedDataService;
import com.intorqa.task.persistence.ParsedDataStorage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class RouterServiceTest {

    @Mock
    private ParsedDataStorage storage;

    private final Integer port = 8080;
    private final String validResponse = "{\"numberOfProcessedFiles\":2,\"numberOfProcessedWords\":3,\"numberOfUniqueProcessedWords\":3,\"mostUsedWords\":[{\"word\":\"word3\",\"numberOfOccurrences\":3},{\"word\":\"word1\",\"numberOfOccurrences\":1},{\"word\":\"word2\",\"numberOfOccurrences\":1}]}";

    @BeforeEach
    public void init(Vertx vertx, VertxTestContext testContext) {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port));

        vertx.deployVerticle(
                new HttpServer(
                        new RouterService(
                                vertx,
                                new ParsedDataService(storage)
                        )
                ),
                options,
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    public void shouldReturnParsedData(Vertx vertx, VertxTestContext testContext) {
        when(storage.getProcessedFiles()).thenReturn(ImmutableSet.of("file1.txt", "file2.txt"));
        when(storage.getProcessedWords()).thenReturn(ImmutableSet.of("word1", "word2", "word3"));
        when(storage.getWordsCount()).thenReturn(new AtomicInteger(3));
        when(storage.getWordsToOccurrences()).thenReturn(ImmutableMap.of("word1", 1, "word2", 1, "word3", 3));

        WebClient client = WebClient.create(vertx);
        client.get(port, "localhost", "/reports")
                .as(BodyCodec.string())
                .send(testContext.succeeding(response -> testContext.verify(() -> {
                            assertThat(response.body()).isEqualTo(validResponse);
                            testContext.completeNow();
                        }))
                );
    }

    @AfterEach
    public void destroy(Vertx vertx) {
        vertx.close();
    }
}
