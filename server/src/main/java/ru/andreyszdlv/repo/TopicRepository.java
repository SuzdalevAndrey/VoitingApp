package ru.andreyszdlv.repo;

import ru.andreyszdlv.model.Topic;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TopicRepository {

    private static final Set<Topic> topics = ConcurrentHashMap.newKeySet();

    public void saveTopic(Topic topic) {
        topics.add(topic);
    }

    public boolean containsTopicByName(String topicName) {
        return topics.contains(new Topic(topicName));
    }
}
