package ru.andreyszdlv.repo;

import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;

import java.util.Map;
import java.util.Optional;

public interface TopicRepository {

    void saveTopics(Map<String, Topic> saveTopics);

    void saveTopic(Topic topic);

    boolean containsTopicByName(String topicName);

    void saveVote(String topicName, Vote vote);

    Map<String, Topic> findAll();

    Optional<Topic> findTopicByName(String topicName);

    void removeVote(String topicName, String voteName);
}