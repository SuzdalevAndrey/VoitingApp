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
import ru.andreyszdlv.service.HandlerService;
import ru.andreyszdlv.service.MessageService;
import ru.andreyszdlv.service.UserService;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Scope("prototype")
@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserService userService;
    private final HandlerFactory handlerFactory;
    private final MessageService messageService;
    private final HandlerService handlerService;

    @Setter
    private Vote vote;

    public void processVoteAnswer(ChannelHandlerContext ctx, String answer) {
        log.info("Processing vote answer: {}", answer);

        answer = answer.trim();
        int option;

        try {
            option = parseVoteOption(answer);
        } catch (NumberFormatException e) {
            log.warn("Invalid vote format: {}. Must be number.", answer);
            messageService.sendMessageByKey(ctx, "error.vote.option.invalid");
            return;
        }

        if (option <= 0) {
            log.warn("Invalid vote option: {}. Option must be positive.", answer);
            messageService.sendMessageByKey(ctx, "error.vote.option.negative");
            return;
        }

        List<AnswerOption> answerOptions = vote.getAnswerOptions();

        if (option > answerOptions.size()) {
            log.warn("Vote option {} out of range. Max allowed: {}", option, answerOptions.size());
            messageService.sendMessageByKey(ctx, "error.vote.option.invalid");
            return;
        }

        String userName = userService.findUserNameByChannel(ctx.channel());
        Set<String> votedUsers = answerOptions.get(option - 1).getVotedUsers();

        if (votedUsers.contains(userName)) {
            log.warn("User \"{}\" already voted for option {}", userName, option);
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