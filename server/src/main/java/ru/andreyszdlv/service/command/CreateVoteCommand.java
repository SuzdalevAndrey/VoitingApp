package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;

public class CreateVoteCommand implements CommandStrategy {

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] parts) {
        if(parts.length != 3 || !parts[2].startsWith("-t=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create vote -t=НазваниеГолосования");
            return;
        }

        String[] params = parts[2].split("=");

        if(params.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String topicName = params[1];

        if(!topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush("Ошибка: не существует топика с именем " + topicName + " !");
            return;
        }

        ctx.pipeline().remove(ctx.handler());

        ctx.pipeline().addLast(new VoteDescriptionHandler(topicName));

        ctx.writeAndFlush("Введите уникальное название для голосования");
    }
}