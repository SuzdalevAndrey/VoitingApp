package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;

public interface CommandStrategy {
    void execute(ChannelHandlerContext ctx, String[] paramsCommand);
}
