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
        }
    }

    private void handleNoParams(ChannelHandlerContext ctx, String[] paramsCommand){
        topicRepository.getAllTopics().forEach(
                topic->ctx.write(
                        String.format(
                                "%s (votes in topic=%s)\n",
                                topic.getName(),
                                topic.getVotes().size()
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

        topicRepository.getAllTopics().stream()
                .filter(topic->topic.getName().equals(topicName))
                .findFirst()
                .ifPresent(topic -> {
                    ctx.write(topic.getName() + "{\n");
                    topic.getVotes().forEach(vote -> ctx.write(vote.toString() + "\n"));
                    ctx.write("}");
                });

        ctx.flush();
    }
}
