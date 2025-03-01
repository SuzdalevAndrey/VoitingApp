package ru.andreyszdlv.validator;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.UserRepository;

@RequiredArgsConstructor
public class AuthenticationValidator {

    private final UserRepository userRepository;

    public boolean isAuthenticated(Channel channel) {
        return userRepository.containsUserByChannelId(channel.id().asLongText());
    }
}
