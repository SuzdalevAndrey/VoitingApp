package ru.andreyszdlv.service.command.user;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserCommandService {

    private final Map<ru.andreyszdlv.enums.UserCommand, UserCommand> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator = new AuthenticationValidator();

    public UserCommandService() {
        commands.put(ru.andreyszdlv.enums.UserCommand.LOGIN, new LoginUserCommand());
        commands.put(ru.andreyszdlv.enums.UserCommand.LOGOUT, new LogoutUserCommand());
        commands.put(ru.andreyszdlv.enums.UserCommand.CREATE_TOPIC, new CreateTopicUserCommand());
        commands.put(ru.andreyszdlv.enums.UserCommand.CREATE_VOTE, new CreateVoteUserCommand());
        commands.put(ru.andreyszdlv.enums.UserCommand.VIEW, new ViewUserCommand());
        commands.put(ru.andreyszdlv.enums.UserCommand.VOTE, new VoteUserCommand());
        commands.put(ru.andreyszdlv.enums.UserCommand.DELETE, new DeleteUserCommand());
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();

        ru.andreyszdlv.enums.UserCommand command = Arrays.stream(ru.andreyszdlv.enums.UserCommand.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            ctx.writeAndFlush("Ошибка: неизвестная команда: " + trimmedCommand);
            return;
        }

        if(!command.equals(ru.andreyszdlv.enums.UserCommand.LOGIN)
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