package ru.andreyszdlv.service.command.server;

import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.file.FileHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class LoadServerCommand implements ServerCommandHandler{

    private final FileHandler fileHandler;

    private final TopicRepository topicRepository;

    @Override
    public void execute(String paramCommand) {
        try {
            topicRepository.saveTopics(fileHandler.load(paramCommand));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
