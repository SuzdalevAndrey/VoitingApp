package ru.andreyszdlv.repo;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, String> users = new ConcurrentHashMap<>();

    @Override
    public void saveUser(String channelId, String username) {
        users.put(channelId, username);
    }

    @Override
    public boolean containsUserByName(String username) {
        return users.containsValue(username);
    }

    @Override
    public String removeUser(String channelId) {
        return users.remove(channelId);
    }

    @Override
    public boolean containsUserByChannelId(String channelId) {
        return users.containsKey(channelId);
    }

    @Override
    public String findUserByChannelId(String channelId) {
        return users.get(channelId);
    }
}