package ru.andreyszdlv.service;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.util.MessageProviderUtil;

@Service
public class MessageService {
    public void sendMessageByKey(ChannelHandlerContext ctx, String messageKey, Object... args) {
        ctx.writeAndFlush(MessageProviderUtil.getMessage(messageKey, args));
    }
}
