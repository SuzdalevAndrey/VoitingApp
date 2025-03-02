package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtil;

@Slf4j
@RequiredArgsConstructor
public class CreateTopicUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing create topic command with params: {}", (Object) paramsCommand);

        if(paramsCommand.length != 1) {
            log.warn("Invalid number of parameters for create topic command");
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.command.create_topic.invalid"));
            return;
        }

        String topicName = ParameterUtil.extractValueByPrefix(paramsCommand[0], "-n=");

        if(topicName == null) {
            log.warn("Create topic command options invalid. Expected: -t=TopicName. Received {}.",
                    (Object) paramsCommand);
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.command.create_topic.invalid"));
            return;
        }

        if(topicName.isBlank()){
            log.warn("Topic name is empty");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
            return;
        }

        if(topicRepository.containsTopicByName(topicName)) {
            log.warn("Topic \"{}\" already exists", topicName);
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.topic.already_exist", topicName));
            return;
        }

        topicRepository.saveTopic(new Topic(topicName));
        log.info("Successfully created topic: {}", topicName);
        ctx.writeAndFlush(MessageProviderUtil
                .getMessage("command.create_topic.success", topicName));
    }
}