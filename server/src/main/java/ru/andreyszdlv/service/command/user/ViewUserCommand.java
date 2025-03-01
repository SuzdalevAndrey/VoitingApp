package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class ViewUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        switch(paramsCommand.length){
            case 0 -> handleNoParams(ctx);
            case 1 -> handleSingleParam(ctx, paramsCommand);
            case 2 -> handleTwoParams(ctx, paramsCommand);
            default -> ctx.writeAndFlush("Ошибка: неверная команда.");
        }
    }

    private void handleNoParams(ChannelHandlerContext ctx){
        topicRepository.findAll().forEach(
                (name, topic) -> ctx.writeAndFlush(String.format(
                        "\"%s\" (votes in topic=%s)\n",
                        name,
                        topic.countVotes())
                )
        );
    }

    private void handleSingleParam(ChannelHandlerContext ctx, String[] paramsCommand){

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");

        if(topicName == null){
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: view -t=НазваниеТопика");
            return;
        }

        if(topicName.isBlank()){
            ctx.writeAndFlush("Ошибка: пустое имя!");
            return;
        }

        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> ctx.writeAndFlush(topic.toString()),
                        () -> ctx.writeAndFlush(String.format(
                                "Ошибка: топик с названием \"%s\" не найден!",
                                topicName)
                        )
                );
    }

    private void handleTwoParams(ChannelHandlerContext ctx, String[] paramsCommand){

        String topicName = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-t=");
        String voteName = ParameterUtils.extractValueByPrefix(paramsCommand[1], "-v=");

        if(topicName == null || voteName == null){
            ctx.writeAndFlush("Ошибка: неверная команда. " +
                    "Пример: view -t=НазваниеТопика -v=НазваниеГолосования");
            return;
        }

        if(topicName.isBlank() || voteName.isBlank()){
            ctx.writeAndFlush("Ошибка: название топика или название голосования пустое!");
            return;
        }

        topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> topic.getVoteByName(voteName)
                                .ifPresentOrElse(
                                        vote -> ctx.writeAndFlush(vote.toString()),
                                        () -> ctx.writeAndFlush(String.format(
                                                "Ошибка: голосование с названием \"%s\" не найдено!",
                                                voteName))
                                ),
                        () -> ctx.writeAndFlush(String.format(
                                "Ошибка: топик с названием \"%s\" не найден!",
                                topicName
                        ))
                );
    }
}