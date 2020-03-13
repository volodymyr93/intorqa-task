package com.intorqa.task.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Let's pretend this is a database interface
public class ProcessedFilesStorage {

    private final Map<String, Long> processedFiles = new ConcurrentHashMap<>();

    public boolean eligibleForProcessing(String fileName, Long lastModified) {
        Long oldValue = processedFiles.put(fileName, lastModified);
        return !lastModified.equals(oldValue);
    }
}
