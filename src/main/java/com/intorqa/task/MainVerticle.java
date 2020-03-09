package com.intorqa.task;

import com.intorqa.task.file.parser.FilesParser;
import com.intorqa.task.file.watcher.FilesWatchRunner;
import com.intorqa.task.file.watcher.FilesWatchService;
import com.intorqa.task.persistence.ParsedDataService;
import com.intorqa.task.persistence.ParsedDataStorage;
import com.intorqa.task.persistence.ProcessedFilesStorage;
import com.intorqa.task.processor.FilesCountProcessor;
import com.intorqa.task.processor.MostPopularWordsCountProcessor;
import com.intorqa.task.processor.WordsCountProcessor;
import com.intorqa.task.processor.WordsUniqueCountProcessor;
import com.intorqa.task.rest.HttpServer;
import com.intorqa.task.rest.RouterService;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.FileSystems;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    //TODO add tests
    //TODO add comments
    //TODO check invalid values in handlers
    //TODO properly close app
    @Override
    public void start() {
        try {
            ParsedDataStorage storage = new ParsedDataStorage();
            vertx.deployVerticle(
                    new FilesWatchRunner(
                            new FilesWatchService(config().getString("monitor.directory")),
                            new ProcessedFilesStorage()
                    )
            );
            vertx.deployVerticle(new FilesParser());
            vertx.deployVerticle(new WordsCountProcessor(storage));
            vertx.deployVerticle(new WordsUniqueCountProcessor(storage));
            vertx.deployVerticle(new MostPopularWordsCountProcessor(storage));
            vertx.deployVerticle(new FilesCountProcessor(storage));
            vertx.deployVerticle(
                    new HttpServer(
                            new RouterService(
                                    vertx,
                                    new ParsedDataService(storage)
                            )
                    )
            );
        } catch (Exception e) {
            log.error("error while initializing application", e);
        }
    }
}
