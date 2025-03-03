package ru.andreyszdlv.service.command.user.createvote;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.factory.RepositoryFactory;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
public class VoteCreationService {

    private final String topicName;

    private String voteName;

    private String description;

    private int numberOfOptions;

    private final List<AnswerOption> options = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private int step = 0;

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    private final List<VoteStepStrategy> stepHandlers;

    public VoteCreationService(String topicName,
                               TopicRepository topicRepository,
                               UserRepository userRepository) {
        this.topicName = topicName;
        stepHandlers = List.of(
                new VoteNameStep(topicRepository, topicName),
                new VoteDescriptionStep(),
                new VoteOptionsCountStep(),
                new VoteOptionsStep()
        );
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    public void processInput(ChannelHandlerContext ctx, String message){
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

    public boolean isMoreOptionsNeeded(){
        return getOptionsCount() < numberOfOptions;
    }

    public void completeVote(ChannelHandlerContext ctx){
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
        ctx.pipeline().addLast(new CommandHandler(new UserCommandService(new AuthenticationValidator(RepositoryFactory.getUserRepository()))));
        log.info("Vote creation completed and pipeline handler updated.");
    }
}