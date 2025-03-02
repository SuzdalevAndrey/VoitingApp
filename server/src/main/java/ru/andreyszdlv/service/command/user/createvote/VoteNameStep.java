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
        String voteName = message.trim().split(" ")[0];
        if(topicRepository.containsVoteByName(topicName, voteName)){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.already_exist", voteName));
            return;
        }
        service.setVoteName(voteName);
        service.nextStep();
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.description"));
    }
}