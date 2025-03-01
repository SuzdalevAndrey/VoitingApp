package ru.andreyszdlv.service.command;

import ru.andreyszdlv.enums.UserCommand;

import java.util.HashMap;
import java.util.Map;

public class ServerCommandService {

    private final Map<UserCommand, CommandStrategy> commands = new HashMap<>();

    public ServerCommandService() {

    }

    public void dispatch(String command) {

    }
}