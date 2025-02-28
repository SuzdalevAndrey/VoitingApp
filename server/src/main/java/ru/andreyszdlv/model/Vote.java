package ru.andreyszdlv.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Vote {

    private final String name;

    private final String description;

    private final String authorName;

    private final List<AnswerOption> answerOptions;

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("Голосование: ").append(name).append("\n")
                .append("Тема: ").append(description).append("\n")
                .append("Варианты ответа{\n");

        for (int i = 0; i < answerOptions.size(); i++) {
            result.append("\tВариант #").append(i + 1).append(": ")
                    .append(answerOptions.get(i).toString()).append("\n");
        }

        result.append("}");

        return result.toString();
    }
}