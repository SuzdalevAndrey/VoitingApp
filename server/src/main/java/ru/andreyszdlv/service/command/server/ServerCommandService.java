package ru.andreyszdlv.service.command.server;

import ru.andreyszdlv.service.command.user.UserCommand;

import java.util.HashMap;
import java.util.Map;

public class ServerCommandService {

    private final Map<ru.andreyszdlv.enums.UserCommand, UserCommand> commands = new HashMap<>();

    public ServerCommandService() {

    }

    public void dispatch(String command) {

    }
}