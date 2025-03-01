package ru.andreyszdlv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class Topic {

    private String name;

    private final Map<String, Vote> votes = new ConcurrentHashMap<>();

    public int countVotes() {
        return votes.size();
    }

    public Optional<Vote> getVoteByName(String name) {
        return Optional.ofNullable(votes.get(name));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Название: \"").append(name).append("\"\nСписок голосований{\n");
        votes.keySet().forEach(name ->
                result.append("\tНазвание: \"").append(name).append("\"\n")
        );
        result.append("}");
        return result.toString();
    }
}