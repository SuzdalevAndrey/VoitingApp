package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.command.user.UserCommandHandler;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtil;

@Slf4j
@RequiredArgsConstructor
public class CreateVoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing create vote command with params: {}", (Object) paramsCommand);

        if(paramsCommand.length != 1) {
            log.warn("Invalid number of parameters for delete command");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.create_vote.invalid"));
            return;
        }

        String topicName = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-t=");

        if(topicName == null) {
            log.warn("Create vote command options invalid. Expected: -t=TopicName. Received: {}",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.command.create_vote.invalid"));
            return;
        }

        if(topicName.isBlank()) {
            log.warn("Topic name is empty");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
            return;
        }

        if(!topicRepository.containsTopicByName(topicName)) {
            log.warn("Topic \"{}\" not found", topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.not_found", topicName));
            return;
        }

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new VoteDescriptionHandler(topicName));

        log.info("Vote creation process started for topic \"{}\"", topicName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("command.create_vote.success"));
    }
}