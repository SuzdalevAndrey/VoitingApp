package ru.andreyszdlv.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private static final Map<String, String> users = new ConcurrentHashMap<>();

    public void saveUser(String channelId, String username) {
        users.put(channelId, username);
    }

    public boolean containsUserByName(String username) {
        return users.containsValue(username);
    }

    public String removeUser(String channelId) {
        return users.remove(channelId);
    }

    public boolean containsUserByChannelId(String channelId) {
        return users.containsKey(channelId);
    }

    public String getUsername(String channelId) {
        return users.get(channelId);
    }
}