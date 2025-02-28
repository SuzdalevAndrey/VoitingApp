package ru.andreyszdlv.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@RequiredArgsConstructor
public class AnswerOption {
    private final String answer;

    private final Set<String> votedUsers = ConcurrentHashMap.newKeySet();

    @Override
    public String toString() {
        return answer + ". Число проголосовавших: " + votedUsers.size();
    }
}
