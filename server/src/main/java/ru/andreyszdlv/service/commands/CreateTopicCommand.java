package ru.andreyszdlv.service.commands;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;

public class CreateTopicCommand implements CommandStrategy {

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] parts) {

        if(parts.length != 3 || !parts[2].startsWith("-n=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create topic -n=НазваниеТопика");
            return;
        }

        String[] params = parts[2].split("=");

        if(params.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String topicName = params[1];

        if(topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush("Ошибка: топик с именем " + topicName + " уже существует!");
            return;
        }

        topicRepository.saveTopic(new Topic(topicName));
        ctx.writeAndFlush("Топик с названием " + topicName + " создан!");
    }
}
