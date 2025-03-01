package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;

public interface UserCommandHandler {
    void execute(ChannelHandlerContext ctx, String[] paramsCommand);
}
