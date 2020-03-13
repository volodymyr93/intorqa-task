package com.intorqa.task.file.parser;

import com.intorqa.task.file.ParsedRecord;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

// This class has naive implementation of parsing.
// I made this because I believe the goal of this task is not a perfect parser but scalable application.
// In order to count words properly file should end with \n character.
@Slf4j
public class FilesParser extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().<JsonArray>consumer("files.changed", message -> {
            log.debug("Message received: " + message.body());
            List<String> files = message.body().stream()
                    .map(Object::toString)
                    .collect(toList());

            files.forEach(this::fileHandler);
        });
    }

    private void fileHandler(String fileName) {
        RecordParser recordParser = RecordParser.newDelimited("\n", bufferedLine -> {
            log.debug("line " + bufferedLine);
            Arrays.stream(bufferedLine.toString().split(" "))
                    .filter(word -> !word.isEmpty())
                    .forEach(word ->
                            vertx.eventBus().publish(                                       // Potentially different processors can be in different application
                                    "words.parsed",                                // or in separate classes like in my implementation.
                                    JsonObject.mapFrom(new ParsedRecord(fileName, word))    // Parsing files and words processing are decoupled. This helps scale application
                            )
                    );
        });

        vertx.fileSystem().open(fileName, new OpenOptions(), result -> {
            AsyncFile asyncFile = result.result();
            asyncFile.handler(recordParser)
                    .endHandler(v -> {
                        asyncFile.close();
                        log.debug("file processed");
                    });
        });
    }
}
