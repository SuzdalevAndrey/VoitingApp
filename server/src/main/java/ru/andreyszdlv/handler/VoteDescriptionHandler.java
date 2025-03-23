package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.service.command.user.createvote.VoteCreationService;

@RequiredArgsConstructor
public class VoteDescriptionHandler extends SimpleChannelInboundHandler<String> {

    private final VoteCreationService voteCreationService;
    private final UserCommandService userCommandService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        voteCreationService.processInput(ctx, message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        userCommandService.dispatch(ctx, UserCommandType.EXIT.getName());
    }
}