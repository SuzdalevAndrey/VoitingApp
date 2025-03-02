package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

@RequiredArgsConstructor
public class VoteNameStep implements VoteStepStrategy {

    private final TopicRepository topicRepository;

    private final String topicName;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        message = message.trim();
        if(topicRepository.containsVoteByName(topicName, message)){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.already_exist"));
            return;
        }
        service.setVoteName(message);
        service.nextStep();
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.description"));
    }
}