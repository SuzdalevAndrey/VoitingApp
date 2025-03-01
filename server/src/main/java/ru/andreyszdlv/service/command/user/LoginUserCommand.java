package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.repo.UserRepository;
import ru.andreyszdlv.util.ParameterUtils;

@RequiredArgsConstructor
public class LoginUserCommand implements UserCommandHandler {

    private final UserRepository userRepository;

    @Override
    public void execute(ChannelHandlerContext ctx, String[] paramsCommand) {

        if (paramsCommand.length != 1) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: login -u=user");
            return;
        }

        String username = ParameterUtils.extractValueByPrefix(paramsCommand[0], "-u=");

        if(username == null) {
            ctx.writeAndFlush("Ошибка: неверная команда. Пример: login -u=user");
            return;
        }

        if(username.isBlank()){
            ctx.writeAndFlush("Ошибка: пустое имя!");
            return;
        }

        if(userRepository.containsUserByName(username)) {
            ctx.writeAndFlush(String.format("Ошибка: пользователь \"%s\" уже есть в системе!", username));
            return;
        }

        userRepository.saveUser(ctx.channel().id().asLongText(), username);
        ctx.writeAndFlush(String.format("Пользователь \"%s\" вошел в систему!", username));
    }
}