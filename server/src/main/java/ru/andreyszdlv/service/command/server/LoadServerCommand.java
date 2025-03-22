package ru.andreyszdlv.service.command.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.ServerCommandType;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.file.FileHandler;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadServerCommand implements ServerCommandHandler {

    private final FileHandler fileHandler;
    private final TopicRepository topicRepository;

    @Override
    public void execute(String paramCommand) {
        log.info("Executing load server command with parameter: \"{}\"", paramCommand);

        try {
            log.info("Attempting load topics from file: \"{}\"", paramCommand);
            topicRepository.saveTopics(fileHandler.load(paramCommand));
            log.info("Successfully loaded and saved topics from file: '{}'", paramCommand);
        } catch (IOException e) {
            log.error("Error load topics from file: \"{}\". Exception: {}", paramCommand, e.getMessage());
        }
    }

    @Override
    public ServerCommandType getType() {
        return ServerCommandType.LOAD;
    }
}