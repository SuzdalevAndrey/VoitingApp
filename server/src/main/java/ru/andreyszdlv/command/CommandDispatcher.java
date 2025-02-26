package ru.andreyszdlv.command;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandDispatcher() {
        //todo файл конфигурации
        commands.put("login", new LoginCommand());
    }

    public void dispatch(String command, ChannelHandlerContext ctx) {
        //todo сделать нормальное разбиение на команды
        String[] parts = command.split(" ");
        Command cmd = commands.getOrDefault(parts[0],
                (c, p) -> ctx.writeAndFlush("Неизвестная команда: " + parts[0] + "\n"));
        cmd.execute(ctx, command);
    }
}