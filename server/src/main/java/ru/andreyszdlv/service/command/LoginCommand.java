package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class LoginCommand implements CommandStrategy {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if (paramsCommand.length != 1 || !paramsCommand[0].startsWith("-u=")) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: login -u=user");
            return;
        }

        String[] paramAndValue = paramsCommand[0].split("=");

        if(paramAndValue.length != 2) {
            ctx.writeAndFlush("Ошибка: неверное имя. " +
                    "Не может быть пустым и не может содержать '='");
            return;
        }

        String username = paramAndValue[1];

        if(userRepository.containsUserName(username)) {
            ctx.writeAndFlush("Ошибка: пользователь " + username + " уже есть в системе!");
            return;
        }

        userRepository.saveUser(ctx.channel(), username);
        ctx.writeAndFlush("Пользователь " + username + " вошел в систему!");
    }
}
