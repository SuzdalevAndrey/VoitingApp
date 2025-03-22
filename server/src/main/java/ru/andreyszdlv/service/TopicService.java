package ru.andreyszdlv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public boolean createTopicIfNotExists(String topicName) {
        synchronized (this) {
            if (topicRepository.containsTopicByName(topicName)) {
                return false;
            }
            topicRepository.saveTopic(new Topic(topicName));
            return true;
        }
    }
}