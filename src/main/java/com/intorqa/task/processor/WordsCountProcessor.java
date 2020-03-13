package com.intorqa.task.processor;

import com.intorqa.task.persistence.ParsedDataStorage;
import io.vertx.core.AbstractVerticle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class WordsCountProcessor extends AbstractVerticle {

    private final ParsedDataStorage storage;

    @Override
    public void start() {
        vertx.eventBus().consumer("words.parsed",
                message -> {
                    log.debug("Ready to count word " + message.body());
                    storage.getWordsCount().incrementAndGet();
                });
    }
}
