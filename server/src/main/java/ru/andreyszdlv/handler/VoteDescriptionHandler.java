package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.factory.RepositoryFactory;
import ru.andreyszdlv.service.command.user.createvote.VoteCreationService;

public class VoteDescriptionHandler extends SimpleChannelInboundHandler<String> {

    private final VoteCreationService voteCreationService;

    public VoteDescriptionHandler(String topicName) {
        voteCreationService = new VoteCreationService(
                topicName,
                RepositoryFactory.getTopicRepository(),
                RepositoryFactory.getUserRepository());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        voteCreationService.processInput(ctx, message);
    }
}