package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class LoginCommand implements Command {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] parts) {
        if (parts.length < 2 || !parts[1].startsWith("-u=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: login -u=user\n");
            return;
        }
        String username = parts[1].split("=")[1];

        if(userRepository.containsUserName(username)) {
            ctx.writeAndFlush("Ошибка: пользователь " + username + " уже есть в системе!");
            return;
        }

        userRepository.saveUser(ctx.channel(), username);
        ctx.writeAndFlush("Пользователь " + username + " вошел в систему!\n");
    }
}
