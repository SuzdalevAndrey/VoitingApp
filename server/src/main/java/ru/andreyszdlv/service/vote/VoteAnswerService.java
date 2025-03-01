package ru.andreyszdlv.service.vote;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.handler.CommandHandler;
import ru.andreyszdlv.model.Vote;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.service.command.user.UserCommandService;

@RequiredArgsConstructor
public class VoteAnswerService {

    private final UserRepository userRepository = new UserRepository();

    private final Vote vote;

    public void answer(ChannelHandlerContext ctx, String answer){
        int count = 0;
        try {
            count = Integer.parseInt(answer);
            if (count <= 0) {
                ctx.writeAndFlush("Ошибка: вариант должен быть положительным числом!");
                return;
            }
        } catch (NumberFormatException e) {
            ctx.writeAndFlush("Ошибка: пожалуйста, введите правильное число вариантов!");
        }

        if(vote.getAnswerOptions().size() < count){
            ctx.writeAndFlush("Ошибка: вариант ответа больше количества вариантов!");
            return;
        }

        String userName = userRepository.getUsername(ctx.channel().id().asLongText());

        if(vote.getAnswerOptions().get(count - 1).getVotedUsers().contains(userName)){
            ctx.writeAndFlush("Ошибка: вы уже проголосовали за этот вариант!");
            return;
        }

        vote.getAnswerOptions().get(count - 1).getVotedUsers().add(userName);
        ctx.writeAndFlush(String.format("Вы успешно проголосовали за #%d вариант", count));

        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(new CommandHandler(new UserCommandService()));
    }
}
