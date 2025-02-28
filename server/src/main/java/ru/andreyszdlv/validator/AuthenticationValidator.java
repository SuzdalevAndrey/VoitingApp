package ru.andreyszdlv.validator;

import io.netty.channel.Channel;
import ru.andreyszdlv.repo.UserRepository;

public class AuthenticationValidator {

    private final UserRepository userRepository = new UserRepository();

    public boolean isAuthenticated(Channel channel) {
        return userRepository.containsUserByChannelId(channel.id().asLongText());
    }
}
