package ru.andreyszdlv.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vote {

    @EqualsAndHashCode.Include
    private final String name;

    private final String description;

    private final String userName;

    private final List<AnswerOption> answerOptions;
}
