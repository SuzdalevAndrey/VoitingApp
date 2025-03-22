package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.TopicService;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.util.ParamUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTopicUserCommand implements UserCommandHandler {

    private final TopicService topicService;
    private final MessageService messageService;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        log.info("Executing create topic command with params: {}", (Object) paramsCommand);

        String topicName = ParamUtil.extractValueByPrefix(paramsCommand[0], "-n=");

        if (!topicService.createTopicIfNotExists(topicName)) {
            log.warn("Topic \"{}\" already exists", topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.topic.already_exist", topicName));
            return;
        }
        messageService.sendMessageByKey(ctx, "command.create_topic.success", topicName);
    }

    @Override
    public UserCommandType getType() {
        return UserCommandType.CREATE_TOPIC;
    }
}