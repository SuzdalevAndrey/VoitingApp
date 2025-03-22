package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.UserService;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParamUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserCommand implements UserCommandHandler {

    private final UserService userService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing login command with params: {}", (Object) paramsCommand);

        String username = ParamUtil.extractValueByPrefix(paramsCommand[0], "-u=");

        if (userService.createUserIfNotExist(ctx.channel(), username)) {
            log.info("User \"{}\" successfully logged", username);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("command.login.success", username));
            return;
        }

        log.warn("User '{}' already exists", username);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("error.user.already_exist", username));
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.LOGIN;
    }
}