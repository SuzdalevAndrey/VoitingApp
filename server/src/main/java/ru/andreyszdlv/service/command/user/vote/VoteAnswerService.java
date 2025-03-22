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
import ru.andreyszdlv.util.MessageProviderUtil;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Scope("prototype")
@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserRepository userRepository;

    private final HandlerFactory handlerFactory;

    @Setter
    private Vote vote;

    public void answer(ChannelHandlerContext ctx, String answer) {
        //todo исправить баг с тем, что во время голосования пользователь может выйти из системы,
        // но на сервере он останется и будет нельзя зайти под тем же именем
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

        if (option > answerOptions.size()) {
            log.warn("Vote option {} out of range. Max allowed: {}", option, answerOptions.size());
            ctx.writeAndFlush(MessageProviderUtil.getMessage("error.vote.option.invalid"));
            return;
        }

        String userName = userRepository.findUserByChannelId(ctx.channel().id().asLongText());

        Set<String> votedUsers = answerOptions.get(option - 1).getVotedUsers();

        if (votedUsers.contains(userName)) {
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
        ctx.pipeline().addLast(handlerFactory.createCommandHandler());
    }
}