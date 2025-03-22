package ru.andreyszdlv.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String getUserNameByChannel(Channel channel) {
        return userRepository.findUserByChannelId(channel.id().asLongText());
    }

    public boolean createUserIfNotExist(Channel channel, String name) {
        synchronized (this) {
            if (userRepository.containsUserByName(name)) {
                return false;
            }
            userRepository.saveUser(channel.id().asLongText(), name);
            return true;
        }
    }
}