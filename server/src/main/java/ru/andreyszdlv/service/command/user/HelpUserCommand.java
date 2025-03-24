package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.MessageService;

@Service
@RequiredArgsConstructor
public class HelpUserCommand implements UserCommandHandler {

    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        messageService.sendMessageByKey(ctx, "help.text");
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.HELP;
    }
}