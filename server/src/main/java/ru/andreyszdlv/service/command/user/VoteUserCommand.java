package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.ParameterUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class VoteUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        if(paramsCommand.length != 2) {
            ctx.writeAndFlush("Ошибка: неверная команда. " +
                    "Пример: vote -t=НазваниеТопика -v=НазваниеГолосования");
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtils.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            ctx.writeAndFlush("Ошибка: неверная команда. " +
                    "Пример: vote -t=НазваниеТопика -v=НазваниеГолосования");
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()){
            ctx.writeAndFlush("Ошибка: название топика или название голосования пустое!");
            return;
        }

        Optional<Topic> topic = topicRepository.findTopicByName(topicName);

        if(topic.isEmpty()) {
            ctx.writeAndFlush(String.format(
                    "Ошибка: топик с названием \"%s\" не найден!",
                    topicName));
            return;
        }

        Optional<Vote> vote = topic.get().getVoteByName(voteName);

        if(vote.isEmpty()) {
            ctx.writeAndFlush(String.format(
                    "Ошибка: голосование с названием \"%s\" не найдено!",
                    voteName));
            return;
        }

        StringBuilder response = new StringBuilder("Варианты ответа:\n");
        List<AnswerOption> options = vote.get().getAnswerOptions();
        for (int i = 0; i < options.size(); i++) {
            response.append(String.format("Вариант #%d: %s\n", (i + 1), options.get(i).getAnswer()));
        }
        response.append("Выберите вариант ответа. Написать нужно только число.");
        ctx.writeAndFlush(response.toString());

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new VoteAnswerHandler(vote.get()));
    }
}