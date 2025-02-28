package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import ru.andreyszdlv.repo.TopicRepository;

@AllArgsConstructor
public class VoteNameStep implements VoteStepStrategy {

    private final TopicRepository topicRepository = new TopicRepository();

    private final String topicName;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        message = message.trim();
        if(topicRepository.containsVoteByName(topicName, message)){
            ctx.writeAndFlush("Ошибка: такое название уже содержится в топике." +
                    " Введите уникальное название:");
            return;
        }
        service.setVoteName(message);
        service.nextStep();
        ctx.writeAndFlush("Введите тему голосования:");
    }
}
