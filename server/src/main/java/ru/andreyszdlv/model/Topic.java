package ru.andreyszdlv.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Topic {

    @EqualsAndHashCode.Include
    private final String name;

}