package ru.andreyszdlv.repo;

import org.springframework.stereotype.Repository;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTopicRepository implements TopicRepository {

    private static final Map<String, Topic> topics = new ConcurrentHashMap<>();

    @Override
    public void saveTopics(Map<String, Topic> saveTopics) {
        topics.putAll(saveTopics);
    }

    @Override
    public void saveTopic(Topic topic) {
        topics.put(topic.getName(), topic);
    }

    @Override
    public boolean containsTopicByName(String topicName) {
        return topics.containsKey(topicName);
    }

    @Override
    public void addVote(String topicName, Vote vote) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Топик \"" + topicName + "\" не найден");
        }
        topic.getVotes().put(vote.getName(), vote);
    }

    @Override
    public boolean containsVoteByTopicNameAndVoteName(String topicName, String voteName) {
        Topic topic = topics.get(topicName);
        return topic != null && topic.getVotes().containsKey(voteName);
    }

    @Override
    public Map<String, Topic> findAll() {
        return topics;
    }

    @Override
    public Optional<Topic> findTopicByName(String topicName) {
        return Optional.ofNullable(topics.get(topicName));
    }

    @Override
    public void removeVote(String topicName, String voteName) {
        findTopicByName(topicName).ifPresent(topic -> topic.getVotes().remove(voteName));
    }
}
