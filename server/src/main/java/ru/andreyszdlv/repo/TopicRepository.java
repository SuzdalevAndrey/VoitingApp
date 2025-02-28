package ru.andreyszdlv.repo;

import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TopicRepository {

    private static final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public void saveTopic(Topic topic) {
        topics.put(topic.getName(), topic);
    }

    public boolean containsTopicByName(String topicName) {
        return topics.containsKey(topicName);
    }

    public void addVote(String topicName, Vote vote) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Топик \"" + topicName + "\" не найден");
        }
        topic.getVotes().put(vote.getName(), vote);
    }

    public boolean containsVoteByName(String topicName, String voteName) {
        Topic topic = topics.get(topicName);
        return topic != null && topic.getVotes().containsKey(voteName);
    }

    public Map<String, Topic> getAllTopics() {
        return topics;
    }

    public Optional<Topic> getTopicByName(String topicName) {
        return Optional.ofNullable(topics.get(topicName));
    }

    public void removeVote(String topicName, String voteName) {
        getTopicByName(topicName).ifPresent(topic -> topic.getVotes().remove(voteName));
    }
}
