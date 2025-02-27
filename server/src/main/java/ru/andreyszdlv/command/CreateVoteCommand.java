package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.repo.TopicRepository;

@AllArgsConstructor
public class CreateVoteCommand implements Command {

    private final TopicRepository topicRepository = new TopicRepository();

    private final CommandHandler commandHandler;

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

        ctx.pipeline().addLast(new VoteDescriptionHandler(topicName));

        ctx.writeAndFlush("Введите уникальное название для голосования");

        ctx.pipeline().remove(commandHandler);
    }
}