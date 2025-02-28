package ru.andreyszdlv.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Vote {

    @EqualsAndHashCode.Include
    private final String name;

    private final String description;

    private final String userName;

    private final List<AnswerOption> answerOptions;

    @Override
    public String toString(){
        return "\tНазвание: " + name + ". Тема: " + description;
    }
}
