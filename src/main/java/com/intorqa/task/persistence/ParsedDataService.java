package com.intorqa.task.persistence;

import com.intorqa.task.rest.dto.NumberOfWordOccurrencesDto;
import com.intorqa.task.rest.dto.ParsedDataDto;
import lombok.AllArgsConstructor;

import java.util.List;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public class ParsedDataService {

    private final ParsedDataStorage storage;
    private static final Integer MOST_POPULAR_LIMIT = 10;

    public ParsedDataDto getParsedData() {
        return new ParsedDataDto(
                storage.getProcessedFiles().size(),
                storage.getWordsCount().get(),
                storage.getProcessedWords().size(),
                getMostUsedWords()
        );
    }

    private List<NumberOfWordOccurrencesDto> getMostUsedWords() {
        return storage.getWordsToOccurrences().entrySet().stream()
                .sorted(comparingByValue(reverseOrder()))
                .limit(MOST_POPULAR_LIMIT)
                .map(entry -> new NumberOfWordOccurrencesDto(entry.getKey(), entry.getValue()))
                .collect(toList());
    }
}
