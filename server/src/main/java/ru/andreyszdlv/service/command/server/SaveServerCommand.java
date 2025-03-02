package ru.andreyszdlv.service.command.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.file.FileHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class SaveServerCommand implements ServerCommandHandler {

    private final FileHandler fileHandler;

    private final TopicRepository topicRepository;

    @Override
    public void execute(String paramCommand) {
        log.info("Executing save server command with parameter: \"{}\"", paramCommand);

        try {
            log.info("Attempting save topics to file: \"{}\"", paramCommand);
            fileHandler.save(paramCommand, topicRepository.findAll());
            log.info("Successfully saved topics to file: \"{}\"", paramCommand);
        } catch (IOException e) {
            log.error("Error save topics to file: \"{}\". Exception: {}", paramCommand, e.getMessage());
        }
    }
}