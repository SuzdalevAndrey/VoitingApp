package ru.andreyszdlv.service.commands;

import io.netty.channel.ChannelHandlerContext;

public interface CommandStrategy {
    void execute(ChannelHandlerContext ctx, String[] parts);
}
