package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;

public interface Command {
    void execute(ChannelHandlerContext ctx, String[] parts);
}
