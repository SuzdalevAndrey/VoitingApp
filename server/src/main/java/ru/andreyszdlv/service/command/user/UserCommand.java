package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;

public interface UserCommand {
    void execute(ChannelHandlerContext ctx, String[] paramsCommand);
}
