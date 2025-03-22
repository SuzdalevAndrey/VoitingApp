package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Service
@Scope("prototype")
public class VoteCreationService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final List<VoteStepStrategy> stepHandlers;

    private final HandlerFactory handlerFactory;

    private String topicName;

    private String voteName;

    private String description;

    private int numberOfOptions;

    private final List<AnswerOption> options = new ArrayList<>();

    private int step = 0;

    public VoteCreationService(TopicRepository topicRepository,
                               UserRepository userRepository,
                               List<VoteStepStrategy> stepHandlers,
                               HandlerFactory handlerFactory) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.stepHandlers = stepHandlers;
        this.handlerFactory = handlerFactory;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
        ((VoteNameStep) this.stepHandlers.get(0)).setTopicName(topicName);
    }

    public void processInput(ChannelHandlerContext ctx, String message) {
        log.info("Processing input for vote creation: step {} with message: {}", step, message);

        stepHandlers.get(step).execute(ctx, message, this);
    }

    public void nextStep() {
        log.info("Moving to next step from step {}", step);
        ++step;
    }

    public void addOption(String option) {
        log.info("Adding new option: \"{}\"", option);
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

        topicRepository.addVote(
                topicName,
                Vote.builder()
                        .name(voteName)
                        .description(description)
                        .authorName(userRepository.findUserByChannelId(ctx.channel().id().asLongText()))
                        .answerOptions(options)
                        .build()
        );
        ctx.writeAndFlush(MessageProviderUtil.getMessage("create_vote.success", voteName, topicName));
        ctx.pipeline().remove(ctx.handler());
        ctx.pipeline().addLast(handlerFactory.createCommandHandler());
        log.info("Vote creation completed and pipeline handler updated.");
    }
}