package ru.andreyszdlv.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Topic {

    @EqualsAndHashCode.Include
    private final String name;

    private final Set<Vote> votes = ConcurrentHashMap.newKeySet();
}