package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.ParameterUtils;

import java.util.Optional;

public class VoteUserCommand implements UserCommand {

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        if(paramsCommand.length != 2) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: vote -t=TopicName -v=VoteName");
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtils.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: vote -t=TopicName -v=VoteName");
            return;
        }

        Optional<Topic> topic = topicRepository.getTopicByName(topicName);

        if(topic.isEmpty()) {
            ctx.writeAndFlush(String.format("Топик с названием \"%s\" не найден", topicName));
            return;
        }

        Optional<Vote> vote = topic.get().getVoteByName(voteName);

        if(vote.isEmpty()) {
            ctx.writeAndFlush(String.format("Голосование с названием \"%s\" не найдено", voteName));
            return;
        }

        for(int i = 0; i < vote.get().getAnswerOptions().size(); i++) {
            ctx.write(String.format(
                    "Вариант #%d: %s\n.",
                    (i+1),
                    vote.get().getAnswerOptions().get(i).getAnswer())
            );
        }

        ctx.write("Выберите вариант ответа. Написать нужно только число.");

        ctx.flush();

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new VoteAnswerHandler(vote.get()));

        //todo Рефактор
    }
}
