package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.repo.InMemoryTopicRepository;
import ru.andreyszdlv.repo.InMemoryUserRepository;
import ru.andreyszdlv.service.command.user.createvote.VoteCreationService;

public class VoteDescriptionHandler extends SimpleChannelInboundHandler<String> {

    private final VoteCreationService voteCreationService;

    public VoteDescriptionHandler(String topicName) {
        voteCreationService = new VoteCreationService(topicName, new InMemoryTopicRepository(), new InMemoryUserRepository());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        voteCreationService.processInput(ctx, message);
    }
}