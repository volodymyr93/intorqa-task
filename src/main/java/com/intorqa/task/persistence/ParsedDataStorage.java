package com.intorqa.task.persistence;

import io.vertx.core.impl.ConcurrentHashSet;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class ParsedDataStorage {

    private final Set<String> processedFiles = new ConcurrentHashSet<>();
    private final AtomicInteger wordsCount = new AtomicInteger(0);
    private final Set<String> processedWords = new ConcurrentHashSet<>();
    private final Map<String, Integer> wordsToOccurrences = new ConcurrentHashMap<>();
}
