package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.vote.VoteCreationService;

public class VoteDescriptionHandler extends SimpleChannelInboundHandler<String> {

    private final VoteCreationService voteCreationService;

    public VoteDescriptionHandler(String topicName) {
        voteCreationService = new VoteCreationService(topicName, new TopicRepository(), new UserRepository());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        voteCreationService.processInput(ctx, message);
    }
}