package ru.andreyszdlv.service.command.server;

import ru.andreyszdlv.enums.ServerCommandType;

import java.util.HashMap;
import java.util.Map;

public class ServerCommandService {

    private final Map<ServerCommandType, Ser> commands = new HashMap<>();

    public ServerCommandService() {

    }

    public void dispatch(String command) {

    }
}