package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;

public class CreateTopicUserCommand implements UserCommandStrategy {

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 1 || !paramsCommand[0].startsWith("-n=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create topic -n=НазваниеТопика");
            return;
        }

        String[] paramAndValue = paramsCommand[0].split("=");

        if(paramAndValue.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String topicName = paramAndValue[1];

        if(topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush("Ошибка: топик с именем " + topicName + " уже существует!");
            return;
        }

        topicRepository.saveTopic(new Topic(topicName));
        ctx.writeAndFlush("Топик с названием " + topicName + " создан!");
    }
}
