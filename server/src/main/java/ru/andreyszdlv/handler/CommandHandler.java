package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.service.CommandService;

public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private final CommandService commandService = new CommandService(this);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) {
        System.out.println("Получена команда: " + command);

        commandService.dispatch(ctx, command);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        commandService.dispatch(ctx, "logout");
    }
}
