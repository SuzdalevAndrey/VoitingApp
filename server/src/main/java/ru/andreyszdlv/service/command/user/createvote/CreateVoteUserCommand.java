package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class CreateVoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 1) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.create_vote.invalid"));
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");

        if(topicName == null) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.create_vote.invalid"));
            return;
        }

        if(topicName.isBlank()) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
            return;
        }

        if(!topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
            return;
        }

        ctx.pipeline().removeLast();

        ctx.pipeline().addLast(new VoteDescriptionHandler(topicName));

        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.create_vote.success"));
    }
}