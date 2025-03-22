package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.repo.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExitUserCommand implements UserCommandHandler {

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        String channelId = ctx.channel().id().asLongText();
        log.info("Executing exit command for user with channelID: {}", channelId);

        String removedUserName = userRepository.removeUser(channelId);
        log.info("User with channelID \"{}\" and name \"{}\" successfully removed", channelId, removedUserName);
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.EXIT;
    }
}