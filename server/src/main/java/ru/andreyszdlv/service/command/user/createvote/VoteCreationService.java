package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Service
@Scope("prototype")
@RequiredArgsConstructor
public class VoteCreationService {

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final List<VoteStepStrategy> stepHandlers;
    private final HandlerFactory handlerFactory;
    private final HandlerService handlerService;
    private final MessageService messageService;

    private final List<AnswerOption> options = new ArrayList<>();

    private String topicName;
    private String voteName;
    private String description;
    private int numberOfOptions;
    private int step = 0;

    public void setTopicName(String topicName) {
        this.topicName = topicName;
        ((VoteNameStep) this.stepHandlers.get(0)).setTopicName(topicName);
    }

    public void processInput(ChannelHandlerContext ctx, String message) {
        message = message.trim();

        stepHandlers.get(step).execute(ctx, message, this);
    }

    public void nextStep() {
        ++step;
    }

    public void addOption(String option) {
        options.add(new AnswerOption(option));
    }

    public int getOptionsCount() {
        return options.size();
    }

    public boolean isMoreOptionsNeeded() {
        return getOptionsCount() < numberOfOptions;
    }

    public void completeVote(ChannelHandlerContext ctx) {
        log.info("Completing vote creation for topic \"{}\" with vote name \"{}\"",
                topicName,
                voteName);

        topicRepository.saveVote(
                topicName,
                Vote.builder()
                        .name(voteName)
                        .description(description)
                        .authorName(userService.findUserNameByChannel(ctx.channel()))
                        .answerOptions(options)
                        .build()
        );
        messageService.sendMessageByKey(ctx, "create_vote.success", voteName, topicName);
        handlerService.switchHandler(ctx, handlerFactory.createCommandHandler());
        log.info("Vote creation completed and pipeline handler updated.");
    }
}