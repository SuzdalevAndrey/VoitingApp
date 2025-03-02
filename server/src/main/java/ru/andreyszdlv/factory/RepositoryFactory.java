package ru.andreyszdlv.factory;

import ru.andreyszdlv.repo.InMemoryTopicRepository;
import ru.andreyszdlv.repo.InMemoryUserRepository;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;

public class RepositoryFactory {
    private static final TopicRepository topicRepository = new InMemoryTopicRepository();
    private static final UserRepository userRepository = new InMemoryUserRepository();

    public static TopicRepository getTopicRepository() {
        return topicRepository;
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }
}