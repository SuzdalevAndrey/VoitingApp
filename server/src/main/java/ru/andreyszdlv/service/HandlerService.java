package ru.andreyszdlv.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;

@Service
public class HandlerService {

    public void switchHandler(ChannelHandlerContext ctx, SimpleChannelInboundHandler<String> newHandler) {
        ctx.pipeline().removeLast();
        ctx.pipeline().addLast(newHandler);
    }
}
