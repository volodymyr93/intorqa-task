package com.intorqa.task.processor;

import com.intorqa.task.file.ParsedRecord;
import com.intorqa.task.persistence.ParsedDataStorage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class WordsUniqueCountProcessor extends AbstractVerticle {

    private final ParsedDataStorage storage;

    @Override
    public void start() {
        vertx.eventBus().<JsonObject>consumer("words.parsed",
                message -> {
                    log.debug("Ready to count unique word " + message.body());
                    ParsedRecord record = message.body().mapTo(ParsedRecord.class);
                    storage.getProcessedWords().add(record.getWord());
                });
    }
}
