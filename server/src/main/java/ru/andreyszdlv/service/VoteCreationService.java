package ru.andreyszdlv.service;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Setter;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.TopicRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.vote.*;

import java.util.ArrayList;
import java.util.List;

@Setter
public class VoteCreationService {

    private final String topicName;

    private String voteName;

    private String description;

    private int numberOfOptions;

    private final List<AnswerOption> options = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private int step = 0;

    private final TopicRepository topicRepository = new TopicRepository();
    private final UserRepository userRepository = new UserRepository();

    private final List<VoteStepStrategy> stepHandlers;

    public VoteCreationService(String topicName) {
        this.topicName = topicName;
        stepHandlers = List.of(
                new NameStep(),
                new DescriptionStep(),
                new OptionsCountStep(),
                new OptionsStep()
        );
    }

    public void processInput(ChannelHandlerContext ctx, String message){
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

    public boolean isMoreOptionsNeeded(){
        return getOptionsCount() < numberOfOptions;
    }

    public void completeVote(ChannelHandlerContext ctx){
        topicRepository.addVote(
                topicName,
                Vote.builder()
                        .name(voteName)
                        .description(description)
                        .userName(userRepository.getUsername(ctx.channel()))
                        .answerOptions(options)
                        .build()
        );
        ctx.writeAndFlush("Голосование \"" + voteName + "\" успешно создано в топике \"" + topicName + "\".");
        ctx.pipeline().remove(ctx.handler());
        ctx.pipeline().addLast(new CommandHandler());
    }
}