package ru.andreyszdlv.service.command.server;

import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.enums.ServerCommandType;
import ru.andreyszdlv.factory.FileHandlerFactory;
import ru.andreyszdlv.factory.RepositoryFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServerCommandService {

    private final Map<ServerCommandType, ServerCommandHandler> commands = new HashMap<>();

    public ServerCommandService() {
        commands.put(ServerCommandType.SAVE,
                new SaveServerCommand(
                        FileHandlerFactory.getFileHandler(),
                        RepositoryFactory.getTopicRepository()));
        commands.put(ServerCommandType.LOAD,
                new LoadServerCommand(
                        FileHandlerFactory.getFileHandler(),
                        RepositoryFactory.getTopicRepository()));
    }

    public void dispatch(String fullCommand) {
        String trimmedCommand = fullCommand.trim();
        log.info("Received server command: {}", trimmedCommand);

        ServerCommandType command = Arrays.stream(ServerCommandType.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            log.warn("Unknown server command received: \"{}\"", trimmedCommand);
            return;
        }

        log.info("Detected server command: {}", command.getName());

        String param = trimmedCommand.substring(command.getName().length()).trim();

        log.info("Executing server command \"{}\" with parameters: {}", command.getName(), param);
        commands.getOrDefault(command,
                        p -> log.error("Command \"{}\" not registered in system", command))
                .execute(param);
    }
}