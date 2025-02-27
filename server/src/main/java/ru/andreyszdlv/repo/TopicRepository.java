package ru.andreyszdlv.repo;

import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;

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

    public void addVote(String topicName, Vote vote) {
        topics.stream().filter(t->t.getName().equals(topicName)).findFirst().ifPresent(t->t.getVotes().add(vote));
    }

    public boolean containsVote(String topicName, Vote vote) {
        return topics.stream().filter(t->t.getName().equals(topicName)).findFirst().get().getVotes().contains(vote);
    }

    public Set<Topic> getAllTopics() {
        return topics;
    }
}
