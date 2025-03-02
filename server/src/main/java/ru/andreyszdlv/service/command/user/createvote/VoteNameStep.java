package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

@Slf4j
@RequiredArgsConstructor
public class VoteNameStep implements VoteStepStrategy {

    private final TopicRepository topicRepository;

    private final String topicName;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        String voteName = message.trim().split(" ")[0];

        log.info("Received vote name: '{}'", voteName);

        if(voteName.isBlank()){
            log.warn("Vote name is empty.");
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.name.empty"));
            return;
        }

        if(topicRepository.containsVoteByName(topicName, voteName)){
            log.warn("Vote \"{}\" already exists for topic \"{}\"", voteName, topicName);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.already_exist", voteName));
            return;
        }

        service.setVoteName(voteName);
        service.nextStep();

        log.info("Vote name set \"{}\".", voteName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.description"));
    }
}