package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class CreateTopicUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 1) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create topic -n=НазваниеТопика");
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-n=");

        if(topicName == null) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create topic -n=НазваниеТопика");
            return;
        }

        if(topicName.isBlank()){
            ctx.writeAndFlush("Ошибка: пустое имя!");
            return;
        }

        if(topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush(String.format("Ошибка: топик с именем \"%s\" уже существует!", topicName));
            return;
        }

        topicRepository.saveTopic(new Topic(topicName));
        ctx.writeAndFlush(String.format("Топик с именем \"%s\" создан!", topicName));
    }
}
