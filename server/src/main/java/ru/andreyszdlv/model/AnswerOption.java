package ru.andreyszdlv.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AnswerOption {
    private final String answer;

    private long count = 0;

    @Override
    public String toString() {
        return answer + ". Число проголосовавших: " + count;
    }
}
