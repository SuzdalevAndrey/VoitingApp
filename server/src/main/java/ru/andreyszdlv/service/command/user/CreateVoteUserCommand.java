package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;

public class CreateVoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 1 || !paramsCommand[0].startsWith("-t=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create vote -t=НазваниеГолосования");
            return;
        }

        String[] paramAndValue = paramsCommand[0].split("=");

        if(paramAndValue.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String topicName = paramAndValue[1];

        if(!topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush("Ошибка: не существует топика с именем " + topicName + " !");
            return;
        }

        ctx.pipeline().remove(ctx.handler());

        ctx.pipeline().addLast(new VoteDescriptionHandler(topicName));

        ctx.writeAndFlush("Введите уникальное название для голосования");
    }
}