package ru.andreyszdlv;

import io.netty.channel.ChannelFuture;

public interface UserInputHandler {
    void handle(ChannelFuture future);
}
