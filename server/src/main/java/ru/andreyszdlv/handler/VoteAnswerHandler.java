package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.InMemoryUserRepository;
import ru.andreyszdlv.service.vote.VoteAnswerService;

public class VoteAnswerHandler  extends SimpleChannelInboundHandler<String> {

    private final VoteAnswerService voteAnswerService;

    public VoteAnswerHandler(Vote vote){
        voteAnswerService = new VoteAnswerService(new InMemoryUserRepository(), vote);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String answer) {
        voteAnswerService.answer(ctx, answer);
    }
}
