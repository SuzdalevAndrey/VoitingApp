package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.command.CommandDispatcher;

public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private final CommandDispatcher commandDispatcher = new CommandDispatcher(this);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) {
        System.out.println("Получена команда: " + command);

        commandDispatcher.dispatch(ctx, command);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        commandDispatcher.dispatch(ctx, "logout");
    }
}
