package ru.andreyszdlv.service.command.server;

import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.file.FileHandler;
import ru.andreyszdlv.service.file.JsonFileHandler;

import java.io.IOException;

public class SaveServerCommand implements ServerCommandHandler {

    private final FileHandler fileHandler = new JsonFileHandler();

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(String paramCommand) {

        try {
            fileHandler.save(paramCommand, topicRepository.getAllTopics());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}
