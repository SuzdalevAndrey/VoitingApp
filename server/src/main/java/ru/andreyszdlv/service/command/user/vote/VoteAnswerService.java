package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.factory.HandlerFactory;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Scope("prototype")
@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserRepository userRepository;
    private final HandlerFactory handlerFactory;
    private final MessageService messageService;
    private final HandlerService handlerService;

    @Setter
    private Vote vote;

    public void processVoteAnswer(ChannelHandlerContext ctx, String answer) {
        log.info("Processing vote answer: {}", answer);
        int option;
        try {
            option = parseVoteOption(answer);
        } catch (NumberFormatException e) {
            messageService.sendMessageByKey(ctx, "error.vote.option.invalid");
            return;
        }

        if (option <= 0) {
            messageService.sendMessageByKey(ctx, "error.vote.option.negative");
            return;
        }

        List<AnswerOption> answerOptions = vote.getAnswerOptions();

        if (option > answerOptions.size()) {
            messageService.sendMessageByKey(ctx, "error.vote.option.invalid");
            return;
        }

        String userName = userRepository.findUserByChannelId(ctx.channel().id().asLongText());
        Set<String> votedUsers = answerOptions.get(option - 1).getVotedUsers();

        if (votedUsers.contains(userName)) {
            messageService.sendMessageByKey(ctx, "error.vote.option.already_choose");
            return;
        }

        votedUsers.add(userName);

        log.info("User \"{}\" successfully voted for option {}", userName, option);
        messageService.sendMessageByKey(ctx, "vote.success", option);

        handlerService.switchHandler(ctx, handlerFactory.createCommandHandler());
    }

    private int parseVoteOption(String answer) {
        return Integer.parseInt(answer);
    }
}