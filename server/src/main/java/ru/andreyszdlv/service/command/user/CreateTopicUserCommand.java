package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class CreateTopicUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 1) {
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.command.create_topic.invalid"));
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-n=");

        if(topicName == null) {
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.command.create_topic.invalid"));
            return;
        }

        if(topicName.isBlank()){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.name.empty"));
            return;
        }

        if(topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush(MessageProviderUtil
                    .getMessage("error.topic.already_exist", topicName));
            return;
        }

        topicRepository.saveTopic(new Topic(topicName));
        ctx.writeAndFlush(MessageProviderUtil
                .getMessage("command.create_topic.success", topicName));
    }
}
