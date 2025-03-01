package ru.andreyszdlv.service.command.server;

import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.file.FileHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class SaveServerCommand implements ServerCommandHandler {

    private final FileHandler fileHandler;

    private final TopicRepository topicRepository;

    @Override
    public void execute(String paramCommand) {
        try {
            fileHandler.save(paramCommand, topicRepository.findAll());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
