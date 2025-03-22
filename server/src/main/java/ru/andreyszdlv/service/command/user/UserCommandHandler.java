package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.enums.UserCommandType;

public interface UserCommandHandler {
    void execute(ChannelHandlerContext ctx, String[] paramsCommand);

    UserCommandType getType();
}
