package com.intorqa.task.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedDataDto {

    private Integer numberOfProcessedFiles;
    private Integer numberOfProcessedWords;
    private Integer numberOfUniqueProcessedWords;
    private List<NumberOfWordOccurrencesDto> mostUsedWords;
}
