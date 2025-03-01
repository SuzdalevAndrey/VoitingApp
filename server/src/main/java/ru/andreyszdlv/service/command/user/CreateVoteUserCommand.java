package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class CreateVoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 1) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create vote -t=НазваниеГолосования");
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");

        if(topicName == null) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: create vote -t=НазваниеГолосования");
            return;
        }

        if(topicName.isEmpty()) {
            ctx.writeAndFlush("Ошибка: пустое имя!");
            return;
        }

        if(!topicRepository.containsTopicByName(topicName)) {
            ctx.writeAndFlush(String.format("Ошибка: не существует топика с именем \"%s\"!", topicName));
            return;
        }

        ctx.pipeline().removeLast();

        ctx.pipeline().addLast(new VoteDescriptionHandler(topicName));

        ctx.writeAndFlush("Введите уникальное название для голосования");
    }
}