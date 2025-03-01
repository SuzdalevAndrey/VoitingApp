package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.enums.UserCommand;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserCommandService {

    private final Map<UserCommand, UserCommandStrategy> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator = new AuthenticationValidator();

    public UserCommandService() {
        commands.put(UserCommand.LOGIN, new LoginUserCommand());
        commands.put(UserCommand.LOGOUT, new LogoutUserCommand());
        commands.put(UserCommand.CREATE_TOPIC, new CreateTopicUserCommand());
        commands.put(UserCommand.CREATE_VOTE, new CreateVoteUserCommand());
        commands.put(UserCommand.VIEW, new ViewUserCommand());
        commands.put(UserCommand.VOTE, new VoteUserCommand());
        commands.put(UserCommand.DELETE, new DeleteUserCommand());
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();

        UserCommand command = Arrays.stream(UserCommand.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            ctx.writeAndFlush("Ошибка: неизвестная команда: " + trimmedCommand);
            return;
        }

        if(!command.equals(UserCommand.LOGIN)
                && !authenticationValidator.isAuthenticated(ctx.channel())) {
            ctx.writeAndFlush("Ошибка: перед выполнением действий надо войти в систему. " +
                    "Пример: login -u=user");
            return;
        }

        String paramsPart = trimmedCommand.substring(command.getName().length()).trim();
        String[] params = paramsPart.isEmpty() ? new String[0] : paramsPart.split("\\s+");

        commands.getOrDefault(command,
                (c, p) -> ctx.writeAndFlush("Ошибка: неизвестная команда: " + trimmedCommand))
                .execute(ctx, params);
    }
}