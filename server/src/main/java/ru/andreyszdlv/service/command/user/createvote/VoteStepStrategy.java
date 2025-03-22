package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface VoteStepStrategy {

    void execute(ChannelHandlerContext ctx, String message, VoteCreationService service);
}