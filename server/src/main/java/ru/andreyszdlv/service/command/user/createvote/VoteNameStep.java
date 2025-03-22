package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.model.Topic;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.MessageService;

import java.util.Optional;

@Slf4j
@Service
@Order(0)
@RequiredArgsConstructor
public class VoteNameStep implements VoteStepStrategy {

    private final TopicRepository topicRepository;
    private final MessageService messageService;

    @Setter
    private String topicName;

    @Override
    public void execute(ChannelHandlerContext ctx, String message, VoteCreationService service) {
        String voteName = message.trim().split(" ")[0];

        log.info("Received vote name: '{}'", voteName);

        if (voteName.isBlank()) {
            log.warn("Vote name is empty.");
            messageService.sendMessageByKey(ctx, "error.vote.name.empty");
            return;
        }

        Optional<Topic> topicOpt = topicRepository.findTopicByName(topicName);

        if (topicOpt.isEmpty() || !topicOpt.get().containsVoteByName(voteName)) {
            log.warn("Vote \"{}\" already exists for topic \"{}\"", voteName, topicName);
            messageService.sendMessageByKey(ctx, "error.vote.already_exist", voteName);
            return;
        }

        service.setVoteName(voteName);
        service.nextStep();

        log.info("Vote name set \"{}\".", voteName);
        messageService.sendMessageByKey(ctx, "vote.description");
    }
}