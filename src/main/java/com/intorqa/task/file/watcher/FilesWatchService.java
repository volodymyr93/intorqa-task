package com.intorqa.task.file.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.Collections.emptyMap;

public class FilesWatchService {

    private final String directory;
    private final WatchService watchService;

    public FilesWatchService(String directory) throws IOException {
        this.directory = directory;
        this.watchService = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(directory);
        path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY); // Currently it looks for new and modified files as required.
    }                                                            // But in my opinion it is better to monitor only new files, because
                                                                 // parsing modified files can produce ambiguous data.
    public Map<String, Long> poll() {
        WatchKey key = watchService.poll();
        if (key != null) {
            Map<String, Long> affectedFiles = new HashMap<>();
            for (WatchEvent event : key.pollEvents()) {
                String filePath = directory + "/" + event.context();
                affectedFiles.put(filePath, new File(filePath).lastModified());
            }
            key.reset();
            return affectedFiles;
        } else {
            return emptyMap();
        }
    }
}
