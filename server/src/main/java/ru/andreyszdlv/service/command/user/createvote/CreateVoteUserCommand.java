package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParamUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateVoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    private final HandlerFactory handlerFactory;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing create vote command with params: {}", (Object) paramsCommand);

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-t=");

        if(!topicRepository.containsTopicByName(topicName)) {
            log.warn("Topic \"{}\" not found", topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
            return;
        }

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(handlerFactory.createVoteDescriptionHandler(topicName));

        log.info("Vote creation process started for topic \"{}\"", topicName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.create_vote.success"));
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.CREATE_VOTE;
    }
}