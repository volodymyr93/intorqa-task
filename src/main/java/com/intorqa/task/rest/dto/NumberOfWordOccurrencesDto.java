package com.intorqa.task.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberOfWordOccurrencesDto {

    private String word;
    private Integer numberOfOccurrences;
}
