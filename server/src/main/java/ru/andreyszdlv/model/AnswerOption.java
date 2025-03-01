package ru.andreyszdlv.model;

import lombok.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerOption {
    private String answer;

    private final Set<String> votedUsers = ConcurrentHashMap.newKeySet();

    @Override
    public String toString() {
        return answer + ". Число проголосовавших: " + votedUsers.size();
    }
}
