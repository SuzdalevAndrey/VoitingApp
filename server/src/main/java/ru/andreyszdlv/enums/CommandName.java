package ru.andreyszdlv.enums;

import lombok.Getter;

@Getter
public enum CommandName {

    LOGIN("login"),
    CREATE_TOPIC("create topic"),
    CREATE_VOTE("create vote"),
    DELETE("delete"),
    VIEW("view"),
    VOTE("vote"),
    LOGOUT("logout");

    private final String name;

    CommandName(String name){
        this.name = name;
    }
}
