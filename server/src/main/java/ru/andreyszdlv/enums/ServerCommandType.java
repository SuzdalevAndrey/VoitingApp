package ru.andreyszdlv.enums;

import lombok.Getter;

@Getter
public enum ServerCommandType {

    LOAD("load"),
    SAVE("save"),
    EXIT("exit");

    private final String name;

    ServerCommandType(String name){
        this.name = name;
    }
}