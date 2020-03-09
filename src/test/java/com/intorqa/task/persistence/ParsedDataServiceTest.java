package com.intorqa.task.persistence;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.intorqa.task.rest.dto.NumberOfWordOccurrencesDto;
import com.intorqa.task.rest.dto.ParsedDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParsedDataServiceTest {

    @Mock
    private ParsedDataStorage storage;
    @InjectMocks
    private ParsedDataService service;

    @Test
    public void shouldReturnParsedData() {
        setParsedDataStorageValues();

        assertThat(service.getParsedData()).isEqualTo(validParsedData());
    }

    private ParsedDataDto validParsedData() {
        return new ParsedDataDto(
                2,
                59,
                14,
                ImmutableList.of(
                        new NumberOfWordOccurrencesDto("word13", 10),
                        new NumberOfWordOccurrencesDto("word12", 9),
                        new NumberOfWordOccurrencesDto("word11", 8),
                        new NumberOfWordOccurrencesDto("word9", 7),
                        new NumberOfWordOccurrencesDto("word8", 6),
                        new NumberOfWordOccurrencesDto("word6", 5),
                        new NumberOfWordOccurrencesDto("word5", 4),
                        new NumberOfWordOccurrencesDto("word4", 3),
                        new NumberOfWordOccurrencesDto("word3", 2),
                        new NumberOfWordOccurrencesDto("word1", 1)
                )
        );
    }

    private void setParsedDataStorageValues() {
        when(storage.getProcessedFiles()).thenReturn(ImmutableSet.of("file1.txt", "file2.txt"));
        when(storage.getProcessedWords()).thenReturn(ImmutableSet.of("word1", "word2", "word3", "word4", "word5", "word6", "word7", "word8", "word9", "word10", "word11", "word12", "word13", "word14"));
        when(storage.getWordsCount()).thenReturn(new AtomicInteger(59));
        when(storage.getWordsToOccurrences()).thenReturn(
                ImmutableMap.<String, Integer>builder()
                        .put("word1", 1)
                        .put("word2", 1)
                        .put("word3", 2)
                        .put("word4", 3)
                        .put("word5", 4)
                        .put("word6", 5)
                        .put("word7", 1)
                        .put("word8", 6)
                        .put("word9", 7)
                        .put("word10", 1)
                        .put("word11", 8)
                        .put("word12", 9)
                        .put("word13", 10)
                        .put("word14", 1)
                        .build());
    }
}
