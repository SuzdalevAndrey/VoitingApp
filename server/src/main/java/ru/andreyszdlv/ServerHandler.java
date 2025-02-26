package ru.andreyszdlv;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.command.CommandDispatcher;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private final CommandDispatcher commandDispatcher = new CommandDispatcher();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) {
        System.out.println("Получена команда: " + command);

        commandDispatcher.dispatch(command, ctx);
    }
}
