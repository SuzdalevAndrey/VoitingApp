package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.service.command.user.vote.VoteAnswerService;

@RequiredArgsConstructor
public class VoteAnswerHandler extends SimpleChannelInboundHandler<String> {

    private final VoteAnswerService voteAnswerService;

    private final UserCommandService userCommandService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String answer) {
        voteAnswerService.processVoteAnswer(ctx, answer);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        userCommandService.dispatch(ctx, UserCommandType.EXIT.getName());
    }
}