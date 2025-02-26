package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandDispatcher() {
        //todo файл конфигурации
        commands.put("login", new LoginCommand());
        commands.put("logout", new LogoutCommand());
    }

    public void dispatch(String fullCommand, ChannelHandlerContext ctx) {
        //todo сделать нормальное разбиение на команды
        String[] parts = fullCommand.split(" ");
        String command = "create".equals(parts[0])?parts[0]+" "+parts[1]:parts[0];
        Command cmd = commands.getOrDefault(command,
                (c, p) -> ctx.writeAndFlush("Неизвестная команда: " + parts[0] + "\n"));
        cmd.execute(ctx, command);
    }
}