package com.intorqa.task.file.watcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class FilesWatchServiceTest {

    @TempDir
    public Path tempDir;
    private FilesWatchService service;

    private final Long timeoutSeconds = 30L;

    @BeforeEach
    private void init() throws IOException {
        service = new FilesWatchService(tempDir.toString());
    }

    @Test
    public void shouldReturnListOfFiles() throws IOException {
        Path filePath = tempDir.resolve(UUID.randomUUID().toString());
        Files.createFile(filePath);
        await().atMost(ofSeconds(timeoutSeconds))
                .until(() ->
                        service.poll().entrySet().stream()
                                .filter(entry -> entry.getKey().equals(filePath.toString())
                                        && entry.getValue().equals(filePath.toFile().lastModified())
                                )
                                .count() == 1
                );
    }

    @Test
    public void shouldReturnEmptyListOfFiles() {
        assertThat(service.poll()).isEmpty();
    }
}
