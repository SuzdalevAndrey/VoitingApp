package ru.andreyszdlv.service.command;

import io.netty.channel.ChannelHandlerContext;
import ru.andreyszdlv.validator.AuthenticationValidator;

import java.util.HashMap;
import java.util.Map;

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
    }

    public void dispatch(ChannelHandlerContext ctx, String fullCommand) {
        String[] parts = fullCommand.split(" ");
        String command = "create".equals(parts[0]) ? parts[0] + " " + parts[1]: parts[0];
        if(!"login".equals(command) && !authenticationValidator.isAuthenticated(ctx.channel())) {
            ctx.writeAndFlush("Ошибка: перед выполнением действий надо войти в систему. Пример: login -u=user");
            return;
        }
        CommandStrategy cmd = commands.getOrDefault(command,
                (c, p) -> ctx.writeAndFlush("Неизвестная команда: " + parts[0]));
        cmd.execute(ctx, parts);
    }
}