package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.repo.UserRepository;

public class LoginCommand implements Command {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void execute(ChannelHandlerContext ctx, String command) {
//        if (command.startsWith("") < 2) {
//            ctx.writeAndFlush("Ошибка: укажите имя пользователя. Пример: login -u=User123\n");
//            return;
//        }
//        String[] username = command.split("=");
//
//        String username = parts[1].split("=")[1];
//        users.put(ctx.channel().remoteAddress().toString(), username);
//        ctx.writeAndFlush("Пользователь " + username + " вошел в систему!\n");
    }
}
