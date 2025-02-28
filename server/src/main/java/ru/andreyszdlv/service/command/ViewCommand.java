package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.TopicRepository;

public class ViewCommand implements CommandStrategy{

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if(paramsCommand.length == 0){
            handleNoParams(ctx, paramsCommand);
        } else if (paramsCommand.length == 1) {
            handleParamT(ctx, paramsCommand);
        } else if (paramsCommand.length == 2) {
            handleParamTAndV(ctx, paramsCommand);
        } else {
            ctx.writeAndFlush("Ошибка: неверная команда.");
        }
    }

    private void handleNoParams(ChannelHandlerContext ctx, String[] paramsCommand){
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

    private void handleParamT(ChannelHandlerContext ctx, String[] paramsCommand){

        if(paramsCommand.length != 1 || !paramsCommand[0].startsWith("-t=")){
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: view -t=NameTopic");
        }

        String[] paramAndValue = paramsCommand[0].split("=");

        if(paramAndValue.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String topicName = paramAndValue[1];

        topicRepository.getTopicByName(topicName)
                .ifPresentOrElse(
                        topic -> ctx.writeAndFlush(topic.toString()),
                        () -> ctx.writeAndFlush(String.format(
                                "Топик с названием \"%s\" не найден", topicName)
                        )
                );
    }

    private void handleParamTAndV(ChannelHandlerContext ctx, String[] paramsCommand){

        if(paramsCommand.length != 2
                || !paramsCommand[0].startsWith("-t=")
                || !paramsCommand[1].startsWith("-v=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: view -t=NameTopic -v=NameVote");
        }

        String[] paramAndValueForT = paramsCommand[0].split("=");
        String[] paramAndValueForV = paramsCommand[1].split("=");

        if(paramAndValueForT.length != 2 || paramAndValueForV.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String topicName = paramAndValueForT[1];
        String voteName = paramAndValueForV[1];

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
}