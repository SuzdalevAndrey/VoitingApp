package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.factory.RepositoryFactory;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.service.command.user.vote.VoteAnswerService;

public class VoteAnswerHandler  extends SimpleChannelInboundHandler<String> {

    private final VoteAnswerService voteAnswerService;

    public VoteAnswerHandler(Vote vote){
        voteAnswerService = new VoteAnswerService(RepositoryFactory.getUserRepository(), vote);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String answer) {
        voteAnswerService.answer(ctx, answer);
    }
}
