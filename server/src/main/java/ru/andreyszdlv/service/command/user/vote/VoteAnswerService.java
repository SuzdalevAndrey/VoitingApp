package ru.andreyszdlv.service.command.user.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.InMemoryUserRepository;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.command.user.UserCommandService;
import ru.andreyszdlv.util.MessageProviderUtil;
import ru.andreyszdlv.validator.AuthenticationValidator;

@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserRepository userRepository;

    private final Vote vote;

    public void answer(ChannelHandlerContext ctx, String answer){
        int option = 0;
        try {
            option = Integer.parseInt(answer);
            if (option <= 0) {
                ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.negative"));
                return;
            }
        } catch (NumberFormatException e) {
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
        }

        if(option > vote.getAnswerOptions().size()){
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
            return;
        }

        String userName = userRepository.findUserByChannelId(ctx.channel().id().asLongText());

        if(vote.getAnswerOptions().get(option - 1).getVotedUsers().contains(userName)){
                ctx.writeAndFlush(MessageProviderUtil
                        .getMessage("error.vote.option.already_choose"));
            return;
        }

        vote.getAnswerOptions().get(option - 1).getVotedUsers().add(userName);
        ctx.writeAndFlush(MessageProviderUtil.getMessage("vote.success", option));

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new CommandHandler(new UserCommandService(new AuthenticationValidator(new InMemoryUserRepository()))));
    }
}