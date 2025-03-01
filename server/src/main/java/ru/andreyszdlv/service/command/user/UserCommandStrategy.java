package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;

public interface UserCommandStrategy {
    void execute(ChannelHandlerContext ctx, String[] paramsCommand);
}
