package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.command.CommandStrategy;
import ru.andreyszdlv.util.ParameterUtils;

import java.util.Optional;

public class DeleteCommand implements CommandStrategy {

    private final TopicRepository topicRepository = new TopicRepository();

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length != 2) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: delete -t=TopicName -v=VoteName");
            return;
        }

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtils.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: delete -t=TopicName -v=VoteName");
            return;
        }

        Optional<Topic> topic = topicRepository.getTopicByName(topicName);

        if(topic.isEmpty()) {
            ctx.writeAndFlush(String.format("Ошибка: топик с названием \"%s\" не найден", topicName));
            return;
        }

        Optional<Vote> vote = topic.get().getVoteByName(voteName);

        if(vote.isEmpty()) {
            ctx.writeAndFlush(String.format("Ошибка: голосование с названием \"%s\" не найдено", voteName));
            return;
        }

        if(!vote.get().getAuthorName()
                .equals(userRepository.getUsername(ctx.channel().id().asLongText()))){
            ctx.writeAndFlush(String.format(
                    "Ошибка: нельзя удалить голосование \"%s\", потому оно создано не вами!",
                    voteName)
            );
            return;
        }

        topicRepository.removeVote(topicName, voteName);
        ctx.writeAndFlush(String.format("Голосование \"%s\" успешно удалено!", voteName));
    }
}
