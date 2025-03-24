package ru.andreyszdlv.service;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;

public interface UserInputHandler {

    void handle(ChannelFuture future, EventLoopGroup group);
}