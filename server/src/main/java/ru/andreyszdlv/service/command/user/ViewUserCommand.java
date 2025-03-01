package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.TopicRepository;

public class ViewUserCommand implements UserCommandHandler {

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {
        switch(paramsCommand.length){
            case 0:
                handleNoParams(ctx);
                break;
            case 1:
                handleSingleParam(ctx, paramsCommand);
                break;
            case 2:
                handleTwoParams(ctx, paramsCommand);
                break;
            default:
                ctx.writeAndFlush("Ошибка: неверная команда.");
        }
    }

    private void handleNoParams(ChannelHandlerContext ctx){
        topicRepository.getAllTopics().forEach(
                (name, topic)->ctx.write(
                        String.format(
                                "%s (votes in topic=%s)\n",
                                name,
                                topic.countVotes()
                        )
                )
        );

        ctx.flush();
    }

    private void handleSingleParam(ChannelHandlerContext ctx, String[] paramsCommand){

        String topicName = extractValue(paramsCommand[0], "-t=");

        if(topicName == null){
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: view -t=NameTopic");
            return;
        }

        topicRepository.getTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> ctx.writeAndFlush(topic.toString()),
                        () -> ctx.writeAndFlush(String.format(
                                "Топик с названием \"%s\" не найден", topicName)
                        )
                );
    }

    private void handleTwoParams(ChannelHandlerContext ctx, String[] paramsCommand){

        String topicName = extractValue(paramsCommand[0], "-t=");
        String voteName = extractValue(paramsCommand[1], "-v=");

        if(topicName == null && voteName == null){
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: view -t=NameTopic -v=NameVote");
            return;
        }

        topicRepository.getTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> topic.getVoteByName(voteName)
                                .ifPresentOrElse(
                                        vote -> ctx.writeAndFlush(vote.toString()),
                                        () -> ctx.writeAndFlush(String.format(
                                                "Голосование с названием \"%s\" не найдено", voteName)
                                        )
                                ),
                        () -> ctx.writeAndFlush(String.format(
                                "Топик с названием \"%s\" не найден", topicName)
                        )
                );
    }

    private String extractValue(String param, String prefix) {
        if (param.startsWith(prefix)) {
            return param.substring(prefix.length());
        }
        return null;
    }
}