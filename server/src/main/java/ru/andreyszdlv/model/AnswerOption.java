package ru.andreyszdlv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerOption {
    private final String answer;

    private long count;
}
