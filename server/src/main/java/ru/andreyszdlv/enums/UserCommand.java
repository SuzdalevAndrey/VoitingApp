package ru.andreyszdlv.enums;

import lombok.Getter;

@Getter
public enum UserCommand {

    LOGIN("login"),
    CREATE_TOPIC("create topic"),
    CREATE_VOTE("create vote"),
    DELETE("delete"),
    VIEW("view"),
    VOTE("vote"),
    LOGOUT("logout");

    private final String name;

    UserCommand(String name){
        this.name = name;
    }
}
