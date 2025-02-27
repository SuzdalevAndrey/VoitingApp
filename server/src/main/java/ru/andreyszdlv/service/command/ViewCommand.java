package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.TopicRepository;

public class ViewCommand implements CommandStrategy{

    private final TopicRepository topicRepository = new TopicRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] parts) {

        //todo Доделать
        topicRepository.getAllTopics().forEach(
                topic->ctx.write(String.format(
                        "%s (votes in topic=%s)\n",
                        topic.getName(),
                        topic.getVotes().size())
                )
        );

        ctx.flush();
    }
}
