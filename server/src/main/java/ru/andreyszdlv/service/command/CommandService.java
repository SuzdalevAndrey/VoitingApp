package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandService {

    private final Map<String, CommandStrategy> commands = new HashMap<>();

    private final AuthenticationValidator authenticationValidator = new AuthenticationValidator();

    public CommandService() {
        //todo файл конфигурации
        commands.put("login", new LoginCommand());
        commands.put("logout", new LogoutCommand());
        commands.put("create topic", new CreateTopicCommand());
        commands.put("create vote", new CreateVoteCommand());
        commands.put("view", new ViewCommand());
        commands.put("vote", new VoteCommand());
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String trimmedCommand = fullCommand.trim();

        Optional<String> matchedCommand = commands.keySet().stream()
                .filter(trimmedCommand::startsWith)
                .findFirst();

        if(matchedCommand.isEmpty()) {
            ctx.writeAndFlush("Неизвестная команда: " + trimmedCommand);
            return;
        }

        String command = matchedCommand.get();
        String paramsPart = trimmedCommand.substring(command.length()).trim();
        String[] params = paramsPart.isEmpty() ? new String[0] : paramsPart.split("\\s+");

        if(!"login".equals(command) && !authenticationValidator.isAuthenticated(ctx.channel())) {
            ctx.writeAndFlush("Ошибка: перед выполнением действий надо войти в систему." +
                    "Пример: login -u=user");
            return;
        }

        commands.get(matchedCommand.get()).execute(ctx, params);
    }
}