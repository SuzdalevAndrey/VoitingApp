package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtil;

@Slf4j
@RequiredArgsConstructor
public class LoginUserCommand implements UserCommandHandler {

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing login command with params: {}", (Object) paramsCommand);

        if (paramsCommand.length != 1) {
            log.warn("Invalid number of parameters for login command");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.login.invalid"));
            return;
        }

        String username = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-u=");

        if(username == null) {
            log.warn("Login command options invalid. Expected: -u=Username. Received: {}",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.login.invalid"));
            return;
        }

        if(username.isBlank()){
            log.warn("Username is empty");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.user.name.empty"));
            return;
        }

        if(userRepository.containsUserByName(username)) {
            log.warn("User '{}' already exists", username);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.user.already_exist", username));
            return;
        }

        String channelId = ctx.channel().id().asLongText();
        userRepository.saveUser(channelId, username);
        log.info("User \"{}\" successfully logged with channelID \"{}\"", username, channelId);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.login.success", username));
    }
}