package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.andreyszdlv.service.command.UserCommandService;

public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private final UserCommandService userCommandService = new UserCommandService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) {
        System.out.println("Получена команда: " + command);

        userCommandService.dispatch(ctx, command);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        userCommandService.dispatch(ctx, "logout");
    }
}
