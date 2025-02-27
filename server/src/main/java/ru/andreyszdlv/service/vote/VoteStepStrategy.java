package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.service.VoteCreationService;

@FunctionalInterface
public interface VoteStepStrategy {
    void execute(ChannelHandlerContext ctx, String message, VoteCreationService service);
}