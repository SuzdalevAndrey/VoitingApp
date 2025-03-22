package ru.andreyszdlv.service.command.server;

import ru.andreyszdlv.enums.ServerCommandType;

public interface ServerCommandHandler {
    void execute(String paramCommand);

    ServerCommandType getType();
}
