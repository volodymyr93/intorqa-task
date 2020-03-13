package com.intorqa.task.persistence;

import org.junit.jupiter.api.Test;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

public class ProcessedFileStorageTest {

    private ProcessedFilesStorage storage = new ProcessedFilesStorage();

    private final String fileName = "fileName";

    @Test
    public void shouldBeEligibleForProcessing() {
        assertThat(storage.eligibleForProcessing(fileName, now().toEpochMilli())).isTrue();
    }

    @Test
    public void shouldNotBeEligibleForProcessingWhenAddedTwice() {
        long lastModified = now().toEpochMilli();
        storage.eligibleForProcessing(fileName, lastModified);

        assertThat(storage.eligibleForProcessing(fileName, lastModified)).isFalse();
    }

    @Test
    public void shouldBeEligibleForProcessingWhenLastModifiedIsDifferent() {
        storage.eligibleForProcessing(fileName, 1L);

        assertThat(storage.eligibleForProcessing(fileName, 2L)).isTrue();
    }
}
