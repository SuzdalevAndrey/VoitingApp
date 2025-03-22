package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.service.command.user.vote.VoteAnswerService;

@RequiredArgsConstructor
public class VoteAnswerHandler extends SimpleChannelInboundHandler<String> {

    private final VoteAnswerService voteAnswerService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String answer) {
        voteAnswerService.answer(ctx, answer);
    }
}