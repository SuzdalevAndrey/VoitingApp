package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.service.command.user.createvote.VoteCreationService;

@RequiredArgsConstructor
public class VoteDescriptionHandler extends SimpleChannelInboundHandler<String> {

    private final VoteCreationService voteCreationService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        voteCreationService.processInput(ctx, message);
    }
}