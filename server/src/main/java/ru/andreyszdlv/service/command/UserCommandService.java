package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.enums.CommandName;
import ru.andreyszdlv.service.command.user.*;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserCommandService {

    private final Map<CommandName, CommandStrategy> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator = new AuthenticationValidator();

    public UserCommandService() {
        commands.put(CommandName.LOGIN, new LoginCommand());
        commands.put(CommandName.LOGOUT, new LogoutCommand());
        commands.put(CommandName.CREATE_TOPIC, new CreateTopicCommand());
        commands.put(CommandName.CREATE_VOTE, new CreateVoteCommand());
        commands.put(CommandName.VIEW, new ViewCommand());
        commands.put(CommandName.VOTE, new VoteCommand());
        commands.put(CommandName.DELETE, new DeleteCommand());
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();

        CommandName command = Arrays.stream(CommandName.values())
                .filter(cmd -> trimmedCommand.startsWith(cmd.getName()))
                .findFirst()
                .orElse(null);

        if (command == null) {
            ctx.writeAndFlush("Ошибка: неизвестная команда: " + trimmedCommand);
            return;
        }

        if(!command.equals(CommandName.LOGIN)
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