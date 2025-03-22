package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.UserService;
import ru.andreyszdlv.util.ParamUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserCommand implements UserCommandHandler {

    private final UserService userService;
    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing login command with params: {}", (Object) paramsCommand);

        String username = ParamUtil.extractValueByPrefix(paramsCommand[0], "-u=");

        if (userService.createUserIfNotExist(ctx.channel(), username)) {
            log.info("User \"{}\" successfully logged", username);
            messageService.sendMessageByKey(ctx, "command.login.success", username);
            return;
        }

        log.warn("User '{}' already exists", username);
        messageService.sendMessageByKey(ctx, "error.user.already_exist", username);
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.LOGIN;
    }
}