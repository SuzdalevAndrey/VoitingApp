package ru.andreyszdlv.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.handler.VoteAnswerHandler;
import ru.andreyszdlv.handler.VoteDescriptionHandler;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.service.command.user.createvote.VoteCreationService;
import ru.andreyszdlv.service.command.user.vote.VoteAnswerService;

@Component
@RequiredArgsConstructor
public class HandlerFactory {

    private final ObjectProvider<UserCommandService> userCommandService;

    private final ObjectProvider<VoteAnswerService> voteAnswerService;

    private final ObjectProvider<VoteCreationService> voteCreationService;

    public CommandHandler createCommandHandler() {
        return new CommandHandler(userCommandService.getIfAvailable());
    }

    public VoteAnswerHandler createVoteAnswerHandler(Vote vote) {
        VoteAnswerService service = voteAnswerService.getIfAvailable();
        service.setVote(vote);
        return new VoteAnswerHandler(service, userCommandService.getIfAvailable());
    }

    public VoteDescriptionHandler createVoteDescriptionHandler(String topicName) {
        VoteCreationService service = voteCreationService.getIfAvailable();
        service.setTopicName(topicName);
        return new VoteDescriptionHandler(service, userCommandService.getIfAvailable());
    }
}
