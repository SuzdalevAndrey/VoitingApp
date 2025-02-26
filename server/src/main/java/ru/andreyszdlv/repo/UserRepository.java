package ru.andreyszdlv.repo;

import java.nio.channels.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private static final Map<Channel, String> users = new ConcurrentHashMap<>();

    public void saveUser(Channel channel, String username) {
        users.put(channel, username);
    }

    public boolean containsUserName(String username) {
        return users.containsValue(username);
    }
}
