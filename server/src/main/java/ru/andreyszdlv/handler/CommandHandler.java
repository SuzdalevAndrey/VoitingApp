package ru.andreyszdlv.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.enums.UserCommandType;
import ru.andreyszdlv.service.command.user.UserCommandService;

@RequiredArgsConstructor
public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private final UserCommandService userCommandService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String command) {
        userCommandService.dispatch(ctx, command);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        userCommandService.dispatch(ctx, UserCommandType.EXIT.getName());
    }
}