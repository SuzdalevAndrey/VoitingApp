package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserRepository userRepository;

    private final Vote vote;

    public void answer(ChannelHandlerContext ctx, String answer){
        int option;
        try {
            option = Integer.parseInt(answer);
            if (option <= 0) {
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.negative"));
                return;
            }
        } catch (NumberFormatException e) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
            return;
        }

        List<AnswerOption> answerOptions = vote.getAnswerOptions();

        if(option > answerOptions.size()){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
            return;
        }

        String userName = userRepository.findUserByChannelId(ctx.channel().id().asLongText());

        Set<String> votedUsers = answerOptions.get(option - 1).getVotedUsers();

        if(votedUsers.contains(userName)){
                ctx.writeAndFlush(MessageProviderUtil
                        .getMessage("error.vote.option.already_choose"));
            return;
        }

        votedUsers.add(userName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.success", option));

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new CommandHandler(new UserCommandService(new AuthenticationValidator(RepositoryFactory.getUserRepository()))));
    }
}