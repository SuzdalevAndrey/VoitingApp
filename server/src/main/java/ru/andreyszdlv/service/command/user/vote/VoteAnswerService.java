package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.andreyszdlv.factory.RepositoryFactory;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.AnswerOption;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserRepository userRepository;

    private final Vote vote;

    public void answer(ChannelHandlerContext ctx, String answer){
        log.info("Processing vote answer: {}", answer);
        int option;
        try {
            option = Integer.parseInt(answer);
            if (option <= 0) {
                log.warn("Invalid vote option: {}. Option must be positive.", answer);
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.negative"));
                return;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid vote format: {}. Must be number.", answer);
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
            return;
        }

        List<AnswerOption> answerOptions = vote.getAnswerOptions();

        if(option > answerOptions.size()){
            log.warn("Vote option {} out of range. Max allowed: {}", option, answerOptions.size());
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
            return;
        }

        String userName = userRepository.findUserByChannelId(ctx.channel().id().asLongText());

        Set<String> votedUsers = answerOptions.get(option - 1).getVotedUsers();

        if(votedUsers.contains(userName)){
            log.warn("User \"{}\" already voted for option {}", userName, option);
            ctx.writeAndFlush(MessageProviderUtil
                        .getMessage("error.vote.option.already_choose"));
            return;
        }

        votedUsers.add(userName);
        log.info("User \"{}\" successfully voted for option {}", userName, option);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.success", option));

        log.info("Switching pipeline handler to CommandHandler after vote");
        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new CommandHandler(new UserCommandService(new AuthenticationValidator(RepositoryFactory.getUserRepository()))));
    }
}