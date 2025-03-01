package ru.andreyszdlv.enums;

import lombok.Getter;

@Getter
public enum ServerCommand {

    LOAD("load"),
    SAVE("save"),
    EXIT("exit");

    private final String name;

    ServerCommand(String name){
        this.name = name;
    }
}
