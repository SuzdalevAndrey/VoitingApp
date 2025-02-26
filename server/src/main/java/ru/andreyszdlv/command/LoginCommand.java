package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class LoginCommand implements Command {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String command) {
        if (!command.startsWith("login -u=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: login -u=user\n");
            return;
        }
        String username = command.split("=")[0];

        if(userRepository.containsUserName(username)) {
            ctx.writeAndFlush("Ошибка: пользователь с таким уже есть в системе!");
            return;
        }

        userRepository.saveUser(ctx.channel(), username);
        ctx.writeAndFlush("Пользователь " + username + " вошел в систему!\n");
    }
}
