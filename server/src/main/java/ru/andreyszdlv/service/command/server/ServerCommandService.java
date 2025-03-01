package ru.andreyszdlv.service.command.server;

import ru.andreyszdlv.enums.ServerCommandType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerCommandService {

    private final Map<ServerCommandType, ServerCommandHandler> commands = new HashMap<>();

    public ServerCommandService() {
        commands.put(ServerCommandType.SAVE, new SaveServerCommand());
        commands.put(ServerCommandType.LOAD, new LoadServerCommand());
    }

    public void dispatch(String fullCommand) {
        String trimmedCommand = fullCommand.trim();

        ServerCommandType command = Arrays.stream(ServerCommandType.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            System.err.println("Ошибка: неизвестная команда: " + trimmedCommand);
            return;
        }

        String param = trimmedCommand.substring(command.getName().length()).trim();

        commands.getOrDefault(command,
                        (p) -> System.err.println("Ошибка: неизвестная команда: " + trimmedCommand))
                .execute(param);
    }
}