package ru.andreyszdlv.repo;

import io.netty.channel.Channel;

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

    public String removeUser(Channel channel) {
        return users.remove(channel);
    }

    public boolean containsChannel(Channel channel) {
        return users.containsKey(channel);
    }

    public String getUsername(Channel channel) {
        return users.get(channel);
    }
}
