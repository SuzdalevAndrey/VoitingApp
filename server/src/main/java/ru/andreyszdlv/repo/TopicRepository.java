package ru.andreyszdlv.repo;

import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;

import java.util.Map;
import java.util.Optional;

public interface TopicRepository {

    void saveTopics(Map<String, Topic> saveTopics);

    void saveTopic(Topic topic);

    boolean containsTopicByName(String topicName);

    void addVote(String topicName, Vote vote);

    boolean containsVoteByTopicNameAndVoteName(String topicName, String voteName);

    Map<String, Topic> findAll();

    Optional<Topic> findTopicByName(String topicName);

    void removeVote(String topicName, String voteName);
}