package ru.andreyszdlv.service.command.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerCommandService {

    private final List<ServerCommandHandler> commands;

    public void dispatch(String fullCommand) {
        String trimmedCommand = fullCommand.trim();
        log.info("Received server command: {}", trimmedCommand);

        commands.stream()
                .filter(cmd -> trimmedCommand.startsWith(cmd.getType().getName()))
                .findFirst()
                .ifPresentOrElse(
                        cmd -> cmd.execute(trimmedCommand.substring(cmd.getType().getName().length()).trim()),
                        () -> log.warn("Unknown server command received: \"{}\"", trimmedCommand)
                );
    }
}