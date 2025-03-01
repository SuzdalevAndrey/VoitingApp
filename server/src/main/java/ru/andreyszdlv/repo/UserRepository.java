package ru.andreyszdlv.repo;

public interface UserRepository {

    void saveUser(String channelId, String username);

    boolean containsUserByName(String username);

    String removeUser(String channelId);

    boolean containsUserByChannelId(String channelId);

    String findUserByChannelId(String channelId);
}