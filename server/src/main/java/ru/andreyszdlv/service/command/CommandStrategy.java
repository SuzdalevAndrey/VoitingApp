package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface CommandStrategy {
    void execute(ChannelHandlerContext ctx, String[] parts);
}
