package ru.andreyszdlv.enums;

import lombok.Getter;

@Getter
public enum UserCommandType {

    LOGIN("login"),
    CREATE_TOPIC("create topic"),
    CREATE_VOTE("create vote"),
    DELETE("delete"),
    VIEW("view"),
    VOTE("vote"),
    EXIT("exit");

    private final String name;

    UserCommandType(String name){
        this.name = name;
    }
}