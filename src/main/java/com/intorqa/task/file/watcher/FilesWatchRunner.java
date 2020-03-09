package com.intorqa.task.file.watcher;

import com.intorqa.task.persistence.ProcessedFilesStorage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
public class FilesWatchRunner extends AbstractVerticle {

    private final FilesWatchService filesWatchService;
    private final ProcessedFilesStorage processedFilesStorage;

    @Override
    public void start() {
        vertx.setPeriodic(config().getLong("files.watch.iteration"), id -> checkForFiles());
    }

    private void checkForFiles() {
        vertx.<List<String>>executeBlocking(
                promise -> {
                    log.debug("monitor");
                    Map<String, Long> changedFiles = filesWatchService.poll();
                    List<String> changedFilesEligibleForProcessing = changedFiles.entrySet().stream()
                            .filter(fileEntry -> !processedFilesStorage.eligibleForProcessing(fileEntry.getKey(), fileEntry.getValue()))
                            .map(Map.Entry::getKey)
                            .collect(toList());
                    promise.complete(changedFilesEligibleForProcessing);
                }, result -> {
                    List<String> changedFiles = result.result();
                    if (!changedFiles.isEmpty()) {
                        vertx.eventBus().publish("files.changed", new JsonArray(changedFiles));
                    }
                }
        );
    }
}
