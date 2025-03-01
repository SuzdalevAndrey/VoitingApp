package ru.andreyszdlv.service.command.server;

import ru.andreyszdlv.enums.UserCommand;
import ru.andreyszdlv.service.command.user.UserCommandStrategy;

import java.util.HashMap;
import java.util.Map;

public class ServerCommandService {

    private final Map<UserCommand, UserCommandStrategy> commands = new HashMap<>();

    public ServerCommandService() {

    }

    public void dispatch(String command) {

    }
}